package fr.medes.android.os;

import android.os.AsyncTask;

/**
 * Specific {@link AsyncTask} for use in activities and simplify configuration changes handling. Between configuration
 * changes a task is still running but the UI is changing. This class allows to attach and detach callback easily from
 * an {@link AsyncTask}.
 * <p/>
 * When attaching a callback to this task, if the method {@link Callback#onAttachedToTask(BaseAsyncTask)} is called
 * allowing the user to update his UI given the task status and retrieving the result if the
 * {@link BaseAsyncTask#onPostExecute(Object)} call was lost on configuration change by calling the
 * {@link BaseAsyncTask#getResult()}.
 * <p/>
 * This basically proxies all the UI callbacks.
 * <p/>
 * 
 * @author Medes-IMPS
 * 
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * 
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private Callback<Params, Progress, Result> callback;
	private Result result;

	/**
	 * Register a callback for this task.
	 * 
	 * @param callback The callback to register
	 */
	public void setCallback(Callback<Params, Progress, Result> callback) {
		this.callback = callback;
		if (callback != null) {
			callback.onAttachedToTask(getStatus(), result);
		}
	}

	/**
	 * If the task is finished there should be a result in there. If the task is running the result is {@code null}.
	 * 
	 * @return The result
	 */
	public Result getResult() {
		return result;
	}

	@Override
	protected void onCancelled() {
		if (callback != null) {
			callback.onCancelled();
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		this.result = result;
		if (callback != null) {
			callback.onPostExecute(result);
		}
	};

	@Override
	protected void onPreExecute() {
		if (callback != null) {
			callback.onPreExecute();
		}
	}

	@Override
	protected void onProgressUpdate(Progress... values) {
		if (callback != null) {
			callback.onProgressUpdate(values);
		}
	};

	/**
	 * Callback for an {@link AsyncTask}. Basically allows to proxy all the UI calls made by an {@link AsyncTask} in a
	 * callback.
	 * 
	 * @author Medes-IMPS
	 * 
	 * @param <Params>
	 * @param <Progress>
	 * @param <Result>
	 */
	public static interface Callback<Params, Progress, Result> {

		/**
		 * Called when a callback is attached to the task. Useful when a manager is taking care of registering and
		 * unregistering the callback for you.
		 * 
		 * @param status The status of the task
		 * @param result The result if any
		 */
		public void onAttachedToTask(Status status, Result result);

		/**
		 * Called when the task is cancelled.
		 */
		public void onCancelled();

		/**
		 * Called when the execution of task is finished and gives the resul back.
		 * 
		 * @param result The result of the task
		 */
		public void onPostExecute(Result result);

		/**
		 * Called before running the task.
		 */
		public void onPreExecute();

		/**
		 * Called each time the task wants to make contribution to the UI thread.
		 * 
		 * @param values The progress values
		 */
		public void onProgressUpdate(Progress... values);

	}

}