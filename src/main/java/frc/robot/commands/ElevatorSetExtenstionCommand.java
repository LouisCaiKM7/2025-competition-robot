package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorSetExtenstionCommand extends Command{
    private DoubleSupplier meters;
    private ElevatorSubsystem elevatorSubsystem;
    public ElevatorSetExtenstionCommand(DoubleSupplier meters,ElevatorSubsystem elevatorSubsystem){
        this.meters = meters;
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }
    @Override
    public void execute(){
        elevatorSubsystem.setTarget(meters.getAsDouble());
    }
}
