package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.elevator.*;
import edu.wpi.first.math.MathUtil;
import frc.robot.RobotConstants;


public class ElevatorZeroingCommand extends Command {
    private ElevatorSubsystem elevator;

    public ElevatorZeroingCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        addRequirements(elevator);
    }

    @Override
    public void execute(){
        elevator.getIo().setElevatorDirectVoltage(-0.5);
    }

    @Override
    public boolean isFinished() {
        return elevator.getIo().isCurrentMax(RobotConstants.ElevatorConstants.ELEVATOR_ZEROING_CURRENT);
    }

    @Override
    public void end(boolean interrupted) {
        elevator.getIo().setElevatorDirectVoltage(0);
        elevator.getIo().resetElevatorPosition();
    }
}