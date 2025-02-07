package frc.robot.subsystems.climber;

import static frc.robot.RobotConstants.CAN_BUS_NAME;
import static frc.robot.RobotConstants.ClimberConstants.CLIMBER_MOTOR_ID;
import static frc.robot.RobotConstants.ClimberConstants.IS_BRAKE;
import static frc.robot.RobotConstants.ClimberConstants.IS_INVERT;
import static frc.robot.RobotConstants.ClimberConstants.REDUCTION;
import static frc.robot.RobotConstants.ClimberConstants.STATOR_CURRENT_LIMIT_AMPS;
import static frc.robot.RobotConstants.ClimberConstants.SUPPLY_CURRENT_LIMIT_AMPS;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.subsystems.roller.RollerIOReal;


public class ClimberIOReal implements ClimberIO{
   
}
