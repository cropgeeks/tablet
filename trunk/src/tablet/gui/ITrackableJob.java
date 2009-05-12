package tablet.gui;

/**
 * Interface for tasks that are likely to be run in a threaded environment,
 * and can provide progress-bar feedback to a GUI as they run.
 */
public interface ITrackableJob
{
	public boolean isIndeterminate();

	public int getMaximum();

	public int getValue();

	public void runJob() throws Exception;

	public void cancelJob();
}