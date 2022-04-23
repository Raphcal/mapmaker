package fr.rca.mapmaker.io;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class ProgressTracker {
	private final ArrayList<String> steps;
	private final HasProgress.Listener progressListener;

	private double progress;
	private double subStep;

	public ProgressTracker(HasProgress.Listener progressListener, String... steps) {
		this.progressListener = progressListener;
		this.steps = new ArrayList<>(Arrays.asList(steps));
		fireProgress();
	}

	public void stepDidEnd(String step) {
		int index = steps.indexOf(step);
		if (index < 0) {
			throw new IllegalArgumentException("Got: " + step + ", but expected one of: " + steps);
		}
		progress = (100.0 / steps.size()) * index;
		fireProgress();
	}

	public void stepHaveSubsteps(int subStepCount) {
		if (subStepCount > 0) {
			this.subStep = (100.0 / steps.size()) / subStepCount;
		} else {
			this.subStep = 0;
		}
	}

	public void subStepDidEnd() {
		progress += subStep;
		fireProgress();
	}

	public void onEnd() {
		progress = 100;
		fireProgress();
	}

	private void fireProgress() {
		if (progressListener != null) {
			progressListener.onProgress((int) progress);
		}
	}
}
