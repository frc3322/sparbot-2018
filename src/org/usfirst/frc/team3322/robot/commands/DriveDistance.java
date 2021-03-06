package org.usfirst.frc.team3322.robot.commands;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team3322.robot.Robot;

/**
 * Drive until the robot is the given distance away from the box. Uses a local
 * PID controller to run a simple PID loop that is only enabled while this
 * command is running. The input is the averaged values of the left and right
 * encoders.
 */
public class DriveDistance extends Command {
	private PIDController m_pid;

	public DriveDistance(double distance) {
		requires(Robot.m_drivetrain);
		m_pid = new PIDController(4, 0, 0, new PIDSource() {
			PIDSourceType m_sourceType = PIDSourceType.kDisplacement;

			@Override
			public double pidGet() {
				return Robot.m_drivetrain.currentDistance();
			}

			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
				m_sourceType = pidSource;
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				return m_sourceType;
			}
		}, d -> Robot.m_drivetrain.tankDrive(d, d));

		m_pid.setAbsoluteTolerance(0.5);
		m_pid.setSetpoint(distance);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		// Get everything in a safe starting state.
		Robot.m_drivetrain.reset();
		m_pid.reset();
		m_pid.enable();
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return m_pid.onTarget();
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		// Stop PID and the wheels
		m_pid.disable();
		Robot.m_drivetrain.drive(0, 0);
	}
}
