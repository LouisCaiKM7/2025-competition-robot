// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.util.FileVersionException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.*;

import frc.robot.auto.basics.AutoActions;
import frc.robot.commands.elevator.ElevatorCommand;
import frc.robot.commands.RumbleCommand;
import frc.robot.commands.elevator.ElevatorDownCommand;
import frc.robot.commands.elevator.ElevatorResetCommand;
import frc.robot.commands.elevator.ElevatorUpCommand;
import frc.robot.display.Display;
import frc.robot.subsystems.apriltagvision.AprilTagVision;
import frc.robot.subsystems.apriltagvision.AprilTagVisionIONorthstar;
import frc.robot.subsystems.elevator.ElevatorIOTalonFX;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.utils.AllianceFlipUtil;
import lombok.Getter;

import org.frcteam6941.looper.UpdateManager;
import org.json.simple.parser.ParseException;

import static edu.wpi.first.units.Units.Seconds;

import java.io.IOException;
import java.util.function.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
    @Getter
    private final UpdateManager updateManager;

    @Getter
    AprilTagVision aprilTagVision = new AprilTagVision(
            this::getAprilTagLayoutType,
            new AprilTagVisionIONorthstar(this::getAprilTagLayoutType, 0),
            new AprilTagVisionIONorthstar(this::getAprilTagLayoutType, 1));
    Swerve swerve = Swerve.getInstance();
    Display display = Display.getInstance();
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(new ElevatorIOTalonFX());
    double lastResetTime = 0.0;

    // The robot's subsystems and commands are defined here...

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        updateManager = new UpdateManager(swerve,
                display);
        updateManager.registerAll();

        configureBindings();
    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be created via the
     * {@link Trigger#Trigger(BooleanSupplier)} constructor with an arbitrary
     * predicate, or via the named factories in {@link
     * CommandGenericHID}'s subclasses for {@link
     * CommandXboxController Xbox}/{@link CommandPS4Controller
     * PS4} controllers or {@link CommandJoystick Flight
     * joysticks}.
     */
    private void configureBindings() {
        swerve.setDefaultCommand(Commands
                .runOnce(() -> swerve.drive(
                                new Translation2d(
                                        -RobotConstants.driverController.getLeftY()
                                                * RobotConstants.SwerveConstants.maxSpeed.magnitude(),
                                        -RobotConstants.driverController.getLeftX()
                                                * RobotConstants.SwerveConstants.maxSpeed.magnitude()),
                                -RobotConstants.driverController.getRightX()
                                        * RobotConstants.SwerveConstants.maxAngularRate.magnitude(),
                                true,
                                false),
                        swerve));

        RobotConstants.driverController.start().onTrue(
                Commands.runOnce(() -> {
                    /*
                        TODO: the reset command will be activated twice when the start button is pressed only once,
                        this is only a temporary solution to avoid execute the command twice within 0.01s,
                        please fix the bug
                    */
                    if (Timer.getFPGATimestamp() - lastResetTime > 0.01) {
                        swerve.resetHeadingController();
                        swerve.resetPose(
                                new Pose2d(
                                        AllianceFlipUtil.apply(
                                                new Translation2d(0, 0)),
                                        swerve.getLocalizer().getLatestPose().getRotation()));
                    }
                    lastResetTime = Timer.getFPGATimestamp();
                }).ignoringDisable(true));

        RobotConstants.operatorController.y().onTrue( //L1
                        Commands.parallel(
                                new ElevatorCommand(() -> RobotConstants.ElevatorConstants.Position[1], elevatorSubsystem),
                                Commands.waitUntil(() -> elevatorSubsystem.getIo().isNearExtension(RobotConstants.ElevatorConstants.Position[1])))
                );

        RobotConstants.operatorController.b().onTrue( //L2
                        Commands.parallel(
                                new ElevatorCommand(() -> RobotConstants.ElevatorConstants.Position[2], elevatorSubsystem),
                                Commands.waitUntil(() -> elevatorSubsystem.getIo().isNearExtension(RobotConstants.ElevatorConstants.Position[2])))
                );

        RobotConstants.operatorController.a().onTrue( //L3
                        Commands.parallel(
                                new ElevatorCommand(() -> RobotConstants.ElevatorConstants.Position[3], elevatorSubsystem),
                                Commands.waitUntil(() -> elevatorSubsystem.getIo().isNearExtension(RobotConstants.ElevatorConstants.Position[3]))
                        )
                );

        RobotConstants.operatorController.x().onTrue( //L4
                        Commands.parallel(
                                new ElevatorCommand(() -> RobotConstants.ElevatorConstants.Position[4], elevatorSubsystem),
                                Commands.waitUntil(() -> elevatorSubsystem.getIo().isNearExtension(RobotConstants.ElevatorConstants.Position[4])))
                );

        RobotConstants.operatorController.rightBumper().onTrue( //REPOSITION
                Commands.sequence(
                        new ElevatorCommand(()->0,elevatorSubsystem).until(() -> elevatorSubsystem.getIo().isNearExtension(0.0)),
                        new RumbleCommand(Seconds.of(2),RobotConstants.operatorController.getHID())
                )
        );

        RobotConstants.operatorController.rightTrigger().whileTrue(new ElevatorDownCommand(elevatorSubsystem));
        RobotConstants.operatorController.leftTrigger().whileTrue(new ElevatorUpCommand(elevatorSubsystem));
        RobotConstants.operatorController.start().onTrue(new ElevatorResetCommand(elevatorSubsystem).ignoringDisable(true));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     * @throws ParseException
     * @throws IOException
     * @throws FileVersionException
     */
    public Command getAutonomousCommand() throws FileVersionException, IOException, ParseException {
        // An example command will be run in autonomous
        return new SequentialCommandGroup(
                AutoActions.waitFor(0.000001),
                AutoActions.followTrajectory(AutoActions.getTrajectory("T_4"), true, true)
        );
    }

    public FieldConstants.AprilTagLayoutType getAprilTagLayoutType() {
//        if (aprilTagsSpeakerOnly.getAsBoolean()) {
//            return FieldConstants.AprilTagLayoutType.SPEAKERS_ONLY;
//        } else if (aprilTagsAmpOnly.getAsBoolean()) {
//            return FieldConstants.AprilTagLayoutType.AMPS_ONLY;
//        } else {
        return FieldConstants.defaultAprilTagType;
//        }
    }

}