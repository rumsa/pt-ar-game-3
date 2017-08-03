package com.playtech.ptargame3.test.step.substeps;

import com.playtech.ptargame3.common.task.LogicResources;
import com.playtech.ptargame3.common.task.Task;
import com.playtech.ptargame3.common.task.TaskState;
import com.playtech.ptargame3.common.task.state.TwoStepState;
import com.playtech.ptargame3.test.ContextConstants;
import com.playtech.ptargame3.test.step.common.AbstractStep;

import java.util.logging.Logger;


public class Sleep500msStep extends AbstractStep {
    private static final Logger logger = Logger.getLogger(Sleep500msStep.class.getName());

    public Sleep500msStep(LogicResources logicResources) {
        super(logicResources);
    }

    @Override
    public TaskState initialState() {
        return TwoStepState.INITIAL;
    }

    @Override
    public boolean canExecute(Task task) {
        if (task.getCurrentState() == TwoStepState.FINAL) {
            long wakeup = task.getContext().get(ContextConstants.WAKEUP_TIME, Long.class);
            return wakeup <= System.currentTimeMillis();
        } else {
            return true;
        }
    }

    @Override
    public void execute(Task task) {
        if (task.getCurrentState() == TwoStepState.MIDDLE) {
            long wakeup = System.currentTimeMillis() + 500;
            task.getContext().put(ContextConstants.WAKEUP_TIME, wakeup);
            getLogicResources().getTestSleepManager().wakeupAt(task, wakeup);
        }
    }
}