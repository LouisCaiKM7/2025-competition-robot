package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorRunCurrentZeroing extends Command{
    private ElevatorDownCommand elevatorDownCommand;
    private ElevatorSubsystem elevatorSubsystem;
    public ElevatorRunCurrentZeroing(ElevatorDownCommand elevatorDownCommand,
    ElevatorSubsystem elevatorSubsystem){
        this.elevatorDownCommand = elevatorDownCommand;
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }
    public void execute(){
        elevatorDownCommand
        .until(()->elevatorSubsystem.current.getValueAsDouble() > 40.0)
        .finallyDo(()->elevatorSubsystem.resetEncoder(0.0));
        
    }
}
