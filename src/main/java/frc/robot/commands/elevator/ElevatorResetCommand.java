package frc.robot.commands.elevator;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.*;

public class ElevatorResetCommand extends Command{
    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorResetCommand(ElevatorSubsystem elevatorSubsystem){
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void execute(){
        elevatorSubsystem.getIo().resetElevatorPosition();
    }
}