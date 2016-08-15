/*
 * Copyright 2016 Sourabh Verma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.psycoder.sectionedrecyclerview.sample;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.psycoder.sectionedrecyclerview.SectionedRecyclerViewAdapter;

public class MainAdapter extends SectionedRecyclerViewAdapter<SectionedRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    public MainAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getSectionCount() {
        return Data.Quotes.length;
    }

    @Override
    public int getItemCount(int section) {
        return 2;
    }

    @Override
    public int getItemViewType(int section, int sectionPosition, int adapterPosition) {
        return sectionPosition % 2 == 0 ? R.layout.li_quote : R.layout.li_color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.li_quote: return new QuoteHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.li_color: return new ColorHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            default: return null;
        }
    }

    private class QuoteHolder extends ViewHolder {

        public TextView mQuote;

        public QuoteHolder(View itemView) {
            super(itemView);
            mQuote = (TextView) itemView.findViewById(R.id.quote);
        }

        @Override
        public void bind(int section, int sectionPosition, int adapterPosition) {
            mQuote.setText(Data.Quotes[section]);
        }

    }

    private class ColorHolder extends ViewHolder {

        public TextView mQuote;

        public ColorHolder(View itemView) {
            super(itemView);
            mQuote = (TextView) itemView.findViewById(R.id.quote);
        }

        @Override
        public void bind(int section, int sectionPosition, int adapterPosition) {
            int color = Color.parseColor(Data.Colors[section]);
            itemView.setBackgroundColor(color);
            mQuote.setText(Data.Quotes2[section]);
            mQuote.setTextColor(getContrastColor(color));
        }

        public int getContrastColor(int color) {
            return Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
        }

    }

}
