package com.techno.vginv.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.techno.vginv.R;

import java.util.ArrayList;
import java.util.Locale;

import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;

public class MenuAdapter extends BaseAdapter {

    private ArrayList<String> mOptions = new ArrayList<>();
    private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();
    private Context context;

    public MenuAdapter(ArrayList<String> options) {
        mOptions = options;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    public void setViewSelected(int position, boolean selected) {

        // Looping through the options in the menu
        // Selecting the chosen option
        for (int i = 0; i < mOptionViews.size(); i++) {
            if (i == position) {
                if (Locale.getDefault().getDisplayLanguage().equals("العربية") || context.getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic")) {
                    mOptionViews.get(i).setSelected(!selected);
                } else {
                    mOptionViews.get(i).setSelected(selected);
                }
            } else {
                mOptionViews.get(i).setSelected(!selected);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String option = mOptions.get(position);
        context = parent.getContext();

        // Using the DuoOptionView to easily recreate the demo
        final DuoOptionView optionView;
        if (convertView == null) {
            optionView = new DuoOptionView(parent.getContext());
        } else {
            optionView = (DuoOptionView) convertView;
        }

        // Using the DuoOptionView's default selectors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (position) {
                case 0:
                    optionView.bind(option,  optionView.getContext().getDrawable(R.drawable.home), null);
                    break;
                case 1:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.categories), null);
                    break;
                case 2:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.projects_icon), null);
                    break;
                case 3:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.friends), null);
                    break;
                case 4:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.add_friends), null);
                    break;
                case 5:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.group_chat), null);
                    break;
                case 6:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.settings), null);
                    break;
                case 7:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.switch_user), null);
                    break;
                case 8:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.guard), null);
                    break;
                case 9:
                    optionView.bind(option, optionView.getContext().getDrawable(R.drawable.ic_person_white_24dp), null);
                    break;

            }
        } else {
            optionView.bind(option, null, null);
        }

        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView);

        return optionView;
    }
}
