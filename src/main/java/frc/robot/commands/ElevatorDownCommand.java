package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorDownCommand extends Command{
    private ElevatorSubsystem elevatorSubsystem;
    public ElevatorDownCommand(ElevatorSubsystem elevatorSubsystem){
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override 
    public void execute(){
        elevatorSubsystem.setVoltage(1);
        System.out.println("andwoin");
    }
}
