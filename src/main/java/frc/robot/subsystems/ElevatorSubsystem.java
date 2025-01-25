package frc.robot.subsystems;


import java.security.PublicKey;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.SubsystemBase;



public class ElevatorSubsystem extends SubsystemBase{
    private final MotionMagicVoltage positionVoltage =
      new MotionMagicVoltage(0.0).withEnableFOC(true);
    private TalonFX m_elevator_left_master = new TalonFX(50, "rio");
    private TalonFX m_elevator_right_child = new TalonFX(51, "rio");
    public StatusSignal<Current> current = m_elevator_left_master.getSupplyCurrent();
    private double targetElevatorVelocity = 0;
    private final StatusSignal<Angle> position = m_elevator_left_master.getPosition();
    
    public ElevatorSubsystem(){
        m_elevator_right_child.setControl(new Follower(50, true));
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        config.Slot0.GravityType = GravityTypeValue.Elevator_Static;
        config.Slot0.kG = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KG.get(); // 0.11591;
        config.Slot0.kS = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KS.get(); // 0.16898;
        config.Slot0.kV = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KV.get(); // 11.3;
        config.Slot0.kA = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KA.get(); // 0.0;
        config.Slot0.kP = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KP.get(); // 150.0;
        config.Slot0.kD = frc.robot.RobotConstants.ElevatorConstants.ELevatorGainClass.ELEVATOR_KD.get(); // 17.53;

        config.CurrentLimits.StatorCurrentLimit = 60.0;
        config.CurrentLimits.StatorCurrentLimitEnable = true;
        config.CurrentLimits.SupplyCurrentLimit = 20.0;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.MotionMagic.MotionMagicAcceleration = 8.0;
        // Estimated from slightly less than motor free speed
        config.MotionMagic.MotionMagicCruiseVelocity =
            50.0 / (frc.robot.RobotConstants.ElevatorConstants.ELEVATOR_GEAR_RATIO * 2 * Math.PI * frc.robot.RobotConstants.ElevatorConstants.ELEVATOR_DRUM_RADIUS_METERS);

        // Carriage position meters in direction of elevator
        config.Feedback.SensorToMechanismRatio =
        frc.robot.RobotConstants.ElevatorConstants.ELEVATOR_GEAR_RATIO / (2 * Math.PI * frc.robot.RobotConstants.ElevatorConstants.ELEVATOR_DRUM_RADIUS_METERS);
        m_elevator_left_master.getConfigurator().apply(config);
        m_elevator_left_master.setPosition(0.0);
        m_elevator_right_child.getConfigurator().apply(config);
        m_elevator_right_child.setPosition(0.0);
        m_elevator_left_master.optimizeBusUtilization();
        m_elevator_right_child.optimizeBusUtilization();
    }
    public void setVoltage(double voltage){
        m_elevator_left_master.setVoltage(voltage);
    }
    public void setElevatorVelocity(double velocityRPM) {
        double velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(velocityRPM);
        m_elevator_left_master.setControl(new VelocityVoltage(
                Units.radiansToRotations(velocityRadPerSec)
        ));
        targetElevatorVelocity = velocityRadPerSec;
    }
    public void stop() {
        m_elevator_left_master.setVoltage(0);
      }
    public void setTarget(double meters) {
        m_elevator_left_master.setControl(positionVoltage.withPosition(meters));
    }
    
    public void resetEncoder(double finalPosition) {
        m_elevator_left_master.setPosition(finalPosition);
    }
    public double getExtensionMeters(){
        return m_elevator_left_master.getPosition().getValueAsDouble();
    }
    public boolean isNearExtension(double expected) {
        return MathUtil.isNear(expected, position.getValueAsDouble(), 0.02);
    }
    
}
