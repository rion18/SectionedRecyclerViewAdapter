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
package me.psycoder.sectionedrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

@SuppressWarnings("unused")
public abstract class SectionedRecyclerViewAdapter<VH extends SectionedRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * The number of sections this RecyclerView would be divided in. Each section has its own
     * section position which makes it easier to deal with complex data source and complex layouts.
     * By default, section count is 1 and section position is same as adapter position
     * @return Number of sections
     */
    public int getSectionCount() {
        return 1;
    }


    /**
     * Get number of items in {@param section}. By default it returns 0.
     * @param section The section for which item count is returned
     * @return Number of items in given section
     */
    public int getItemCount(int section) {
        return 0;
    }

    /**
     * Returns the total item count by adding items in all sections. Override this if you want to
     * use the SectionedRecyclerViewAdapter as a normal {@link android.support.v7.widget.RecyclerView.Adapter}
     * for whatever reason.
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < getSectionCount(); i++)
            count += getItemCount(i);
        return count;
    }

    /**
     * Override this if you want to
     * use the SectionedRecyclerViewAdapter as a normal {@link android.support.v7.widget.RecyclerView.Adapter}
     * for whatever reason.
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        int pos = position, sectionItemCount;
        for (int i = 0, sectionCount = getSectionCount(); i < sectionCount; i++) {
            sectionItemCount = getItemCount(i);
            if (pos < sectionItemCount) {
                return getItemViewType(i, pos, position);
            }
            pos -= sectionItemCount;
        }
        throw new IllegalStateException("Value of position " + position + " is out of bounds");
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling. By default it returns value of
     * {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     * @param section Section of the item
     * @param sectionPosition Position of item in section
     * @param adapterPosition Absolute position of item in the adapter
     * @return integer value identifying the type of the view needed to represent the item at
     *                 <code>position</code>. Type codes need not be contiguous.
     */
    public int getItemViewType(int section, int sectionPosition, int adapterPosition) {
        return super.getItemViewType(adapterPosition);
    }


    /**
     * Used by {@link SectionedRecyclerViewAdapter} to call {@link #onBindViewHolder(ViewHolder, int, int, int)}
     * with section count and section position
     * Use {@link #onBindViewHolder(SectionedRecyclerViewAdapter.ViewHolder, int, int, int)} instead
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.configure(this);
        int pos = position, sectionItemCount;
        for (int i = 0, sectionCount = getSectionCount(); i < sectionCount; i++) {
            sectionItemCount = getItemCount(i);
            if (pos < sectionItemCount) {
                onBindViewHolder(holder, i, pos, position);
                return;
            }
            pos -= sectionItemCount;
        }
        throw new IllegalStateException("Value of position " + position + " is out of bounds");
    }

    /**
     * Called by @{link SectionedRecyclerViewAdapter} to display the data at the specified position.
     * No need to override this method. Instead use {@link ViewHolder#bind(int, int, int) bind} method of
     * {@link ViewHolder}
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param section Section of the item
     * @param sectionPosition Position of item in section
     * @param adapterPosition Absolute position of item in the adapter
     */
    public void onBindViewHolder(VH holder, int section, int sectionPosition, int adapterPosition) {
        holder.bind(section, sectionPosition, adapterPosition);
    }

    /**
     * Changes position of item from relative to absolute
     * @param section The section of current item who's relative position is given
     * @param sectionPosition The section position of item in the given section
     * @return Absolute adapter position of item
     */
    protected int sectionToAdapterPosition(int section, int sectionPosition) {
        int adapterPosition = 0;
        for (int i = 0; i < section; i++)
            adapterPosition += getItemCount(i);
        return adapterPosition + sectionPosition;
    }

    /**
     * Changes position of item from absolute to relative
     * @param adapterPosition The absolute position of item
     * @return An array of {@code int} where first value is section to which the item belongs
     * and second value is the relative position inside this section
     */
    protected int[] adapterToSectionPosition(int adapterPosition) {
        int sectionItemCount;
        for (int i = 0, sectionCount = getSectionCount(); i < sectionCount; i++) {
            sectionItemCount = getItemCount(i);
            if (adapterPosition < sectionItemCount) {
                return new int[] { i, adapterPosition };
            }
            adapterPosition -= sectionItemCount;
        }
        return new int[] { RecyclerView.NO_POSITION, RecyclerView.NO_POSITION };
    }

    /**
     * Changes position of item from absolute to relative. The passed pair is updated with new value.
     * @param adapterPosition The absolute position of item
     * @return An array of {@code int} where first value is section to which the item belongs
     * and second value is the relative position inside this section
     */
    protected Pair<Integer, Integer> adapterToSectionPosition(int adapterPosition, Pair<Integer, Integer> pos) {
        int sectionItemCount;
        for (int i = 0, sectionCount = getSectionCount(); i < sectionCount; i++) {
            sectionItemCount = getItemCount(i);
            if (adapterPosition < sectionItemCount) {
                pos.first = i; pos.second = adapterPosition;
                return pos;
            }
            adapterPosition -= sectionItemCount;
        }
        pos.first = RecyclerView.NO_POSITION; pos.second = RecyclerView.NO_POSITION;
        return pos;
    }

    /**
     * Notifies that item at relative position {@code sectionPosition} in section {@code section}
     * has changed by calling {@link #notifyItemChanged(int)}
     * @param section The section number of item
     * @param sectionPosition The relative position of item in section
     */
    public void notifyItemChanged(int section, int sectionPosition) {
        notifyItemChanged(sectionToAdapterPosition(section, sectionPosition));
    }

    /**
     * Notifies that {@code itemCount} number of items starting at relative position
     * {@code sectionPositionStart} in section {@code section} have changed by calling
     * {@link #notifyItemRangeChanged(int, int)}
     * @param section The section number of item
     * @param sectionPositionStart The starting relative position of item in section
     * @param itemCount number of items that changed
     */
    public void notifyItemRangeChanged(int section, int sectionPositionStart, int itemCount) {
        notifyItemRangeChanged(sectionToAdapterPosition(section, sectionPositionStart), itemCount);
    }

    /**
     * Notifies that item at relative position {@code sectionPosition} in section {@code section}
     * is inserted by calling {@link #notifyItemInserted(int)}
     * @param section The section number of item
     * @param sectionPosition The relative position of item in section
     */
    public void notifyItemInserted(int section, int sectionPosition) {
        notifyItemInserted(sectionToAdapterPosition(section, sectionPosition));
    }

    /**
     * Notifies that item in {@code section} has moved from relative position {@code fromPosition}
     * to position {@code toPosition} by calling {@link #notifyItemMoved(int, int)}
     * @param section The section number of item
     * @param fromRelativePosition The initial relative position of item
     * @param toRelativePosition The final relative position of item
     */
    public void notifyItemMoved(int section, int fromRelativePosition, int toRelativePosition) {
        int absoluteFromPosition = sectionToAdapterPosition(section, fromRelativePosition);
        int absoluteToPosition = sectionToAdapterPosition(section, toRelativePosition);
        notifyItemMoved(absoluteFromPosition, absoluteToPosition);
    }

    /**
     * Notifies that item in {@code section} has moved from relative position {@code fromPosition}
     * to position {@code toPosition} by calling {@link #notifyItemMoved(int, int)}
     * @param fromSection The section number of item before moving
     * @param fromRelativePosition The initial relative position of item
     * @param toSection The section number of item after moving
     * @param toRelativePosition The final relative position of item
     */
    public void notifyItemMoved(int fromSection, int fromRelativePosition, int toSection, int toRelativePosition) {
        int absoluteFromPosition = sectionToAdapterPosition(fromSection, fromRelativePosition);
        int absoluteToPosition = sectionToAdapterPosition(toSection, toRelativePosition);
        notifyItemMoved(absoluteFromPosition, absoluteToPosition);
    }

    /**
     * Notifies that {@code itemCount} number of items starting at relative position
     * {@code sectionPositionStart} in section {@code section} are inserted by calling
     * {@link #notifyItemRangeInserted(int, int)}
     * @param section The section number of item
     * @param sectionPositionStart The starting relative position of item in section
     * @param itemCount number of items that changed
     */
    public void notifyItemRangeInserted(int section, int sectionPositionStart, int itemCount) {
        notifyItemRangeInserted(sectionToAdapterPosition(section, sectionPositionStart), itemCount);
    }

    /**
     * Notifies that item at relative position {@code sectionPosition} in section {@code section}
     * is removed by calling {@link #notifyItemRemoved(int)}
     * @param section The section number of item
     * @param sectionPosition The relative position of item in section
     */
    public void notifyItemRemoved(int section, int sectionPosition) {
        notifyItemRemoved(sectionToAdapterPosition(section, sectionPosition));
    }

    /**
     * Notifies that {@code itemCount} number of items starting at relative position
     * {@code sectionPositionStart} in section {@code section} are removed by calling
     * {@link #notifyItemRangeRemoved(int, int)}
     * @param section The section number of item
     * @param sectionPositionStart The starting relative position of item in section
     * @param itemCount number of items that changed
     */
    public void notifyItemRangeRemoved(int section, int sectionPositionStart, int itemCount) {
        notifyItemRangeRemoved(sectionToAdapterPosition(section, sectionPositionStart), itemCount);
    }

    /**
     * Base ViewHolder for {@link SectionedRecyclerViewAdapter} which has methods
     * {@link #getSectionNumber()} and {@link #getSectionPosition()} to get section number and
     * relative position inside that section of this view holder
     */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        private SectionedRecyclerViewAdapter mAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Configure the view holder with instance of parent {@link SectionedRecyclerViewAdapter}
         * @param adapter Parent {@link SectionedRecyclerViewAdapter}
         */
        void configure(SectionedRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        /**
         * Called when onBindViewHolder is called on the adapter. Use this to bind any data to the views.
         * Use the constructor to bind any listeneres
         * @param section section count of the view holder
         * @param sectionPosition position in the section
         * @param adapterPosition absolute position in the adapter
         */
        public void bind(int section, int sectionPosition, int adapterPosition) {

        }

        /**
         * @return position of section of this view holder
         */
        public int getSectionNumber() {
            if (mAdapter != null)
                return mAdapter.adapterToSectionPosition(getAdapterPosition())[0];
            else
                return RecyclerView.NO_POSITION;
        }

        /**
         * @return relative position of this view holder in its section
         */
        public int getSectionPosition() {
            if (mAdapter != null)
                return mAdapter.adapterToSectionPosition(getAdapterPosition())[1];
            else
                return RecyclerView.NO_POSITION;
        }

        /**
         * Use this to get position of data based on adapter position or section position in case of
         * a complex data source
         * @return position of data in data source
         */
        public int getDataPosition() {
            return getAdapterPosition();
        }

    }

}
