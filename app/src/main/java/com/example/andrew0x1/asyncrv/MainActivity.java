package com.example.andrew0x1.asyncrv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
	private static class ViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView name;
		private final ProgressBar value;

		ViewHolder(View itemView)
		{
			super(itemView);

			name = (TextView)itemView.findViewById(R.id.tv_name);
			value = (ProgressBar)itemView.findViewById(R.id.pb_value);
		}

		void bind(AsyncDataUpdater.TimelineItem item)
		{
			name.setText(item.name);
			value.setProgress((int)item.value);
		}
	}

	private static class Adapter extends RecyclerView.Adapter<ViewHolder>
									implements AsyncDataUpdater.DataChangeListener
	{
		private final AsyncDataUpdater _data;

		Adapter(AsyncDataUpdater data)
		{
			_data = data;
			_data.setDataChangeListener(this);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			View v = LayoutInflater.from(parent.getContext())
								   .inflate(R.layout.list_item, parent, false);
			return new ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position)
		{
			holder.bind(_data.getItem(position));
		}

		@Override
		public int getItemCount()
		{
			return _data.getItemsCount();
		}

		@Override
		public void onDataChanged(ArrayList<AsyncDataUpdater.Diff> diffs)
		{
			//Apply changes.
			for(AsyncDataUpdater.Diff d : diffs)
			{
				if(d.command == 0)
					notifyItemInserted(d.position);
				else if(d.command == 1)
					notifyItemChanged(d.position);
				else if(d.command == 2)
					notifyItemRemoved(d.position);
			}
		}
	}

	private AsyncDataUpdater _data = new AsyncDataUpdater();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RecyclerView rv_content = (RecyclerView)findViewById(R.id.rv_content);
		rv_content.setLayoutManager(new LinearLayoutManager(this));
		rv_content.setAdapter(new Adapter(_data));

		Button btn_add = (Button)findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				_data.updateDataAsync();
			}
		});
	}
}
