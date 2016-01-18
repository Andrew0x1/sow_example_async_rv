package com.example.andrew0x1.asyncrv;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Random;

public class AsyncDataUpdater
{
	/**
	 * Example data entity. We will use it
	 * in our RecyclerView.
	 */
	public static class TimelineItem
	{
		public final String name;
		public final float value;

		public TimelineItem(String name, float value)
		{
			this.name = name;
			this.value = value;
		}
	}

	/**
	 * That's how we will apply our data changes
	 * on the RecyclerView.
	 */
	public static class Diff
	{
		// 0 - ADD; 1 - CHANGE; 2 - REMOVE;
		final int command;
		final int position;

		Diff(int command, int position)
		{
			this.command = command;
			this.position = position;
		}
	}

	/**
	 * And that's how we will notify the RecyclerView
	 * about changes.
	 */
	public interface DataChangeListener
	{
		void onDataChanged(ArrayList<Diff> diffs);
	}


	private static class TaskResult
	{
		final ArrayList<Diff> diffs;
		final ArrayList<TimelineItem> items;

		TaskResult(ArrayList<TimelineItem> items, ArrayList<Diff> diffs)
		{
			this.diffs = diffs;
			this.items = items;
		}
	}

	private class InsertEventsTask extends AsyncTask<Void, Void, TaskResult>
	{
		//NOTE: this is copy of the original data.
		private ArrayList<TimelineItem> _old_items;

		InsertEventsTask(ArrayList<TimelineItem> items)
		{
			_old_items = items;
		}

		@Override
		protected TaskResult doInBackground(Void... params)
		{
			ArrayList<Diff> diffs = new ArrayList<>();

			try
			{
				//TODO: long operation(Database, network, ...).
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			//Some crazy manipulation with data...
			//NOTE: we change the copy of the original data!
			Random rand = new Random();
			for(int i = 0; i < 10; i ++)
			{
				float rnd = rand.nextFloat() * 100.0f;
				for(int j = 0; j < _old_items.size(); j++)
				{
					if(_old_items.get(j).value > rnd)
					{
						TimelineItem item = new TimelineItem("Item " + rnd, rnd);
						//Change data.
						_old_items.add(j, item);
						//Log the changes.
						diffs.add(new Diff(0, j));
						break;
					}
				}
			}

			for(int i = 0; i < 5; i ++)
			{
				int rnd_index = rand.nextInt(_old_items.size());
				//Change data.
				_old_items.remove(rnd_index);
				//Log the changes.
				diffs.add(new Diff(2, rnd_index));
			}
			//...

			return new TaskResult(_old_items, diffs);
		}

		@Override
		protected void onPostExecute(TaskResult result)
		{
			super.onPostExecute(result);

			//Apply the new data in the UI thread.
			_items = result.items;
			if(_listener != null)
				_listener.onDataChanged(result.diffs);
		}
	}

	private DataChangeListener _listener;
	private InsertEventsTask _task = null;

	/** Managed data. */
	private ArrayList<TimelineItem> _items = new ArrayList<>();

	public AsyncDataUpdater()
	{
		// Some test data.
		for(float i = 10.0f; i <= 100.0f; i += 10.0f)
			_items.add(new TimelineItem("Item " + i, i));
	}

	public void setDataChangeListener(DataChangeListener listener)
	{
		_listener = listener;
	}

	public void updateDataAsync()
	{
		if(_task != null)
			_task.cancel(true);

		// NOTE: we should to make the new copy of the _items array.
		_task = new InsertEventsTask(new ArrayList<>(_items));
		_task.execute();
	}

	public int getItemsCount()
	{
		return _items.size();
	}

	public TimelineItem getItem(int index)
	{
		return _items.get(index);
	}
}
