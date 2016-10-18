package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Package org.kteam.palm.adapter
 * @Project CommonProj
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 15:45
 */
public class BaseFixedSizeListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected int totalSize;

    protected LinkedList<T> mDataList;

    public BaseFixedSizeListAdapter(Integer totalSize) {
        mDataList = new LinkedList<>();
        this.totalSize = totalSize;
    }

    public void clearData() {
        mDataList.clear();
    }

    public void setData(List<T> list) {
        int size = list.size();
        int csize = mDataList.size();
        int handingSize = size+csize;
        if(handingSize <= this.totalSize){
            mDataList.addAll(list);
        }else{
            int extraCount = handingSize - this.totalSize;
            for(int i=0;i<extraCount;i++){
                mDataList.removeFirst();
            }
            mDataList.addAll(list);
        }
    }

    private void appendData(T t) {
        int csize = mDataList.size();
        int handingSize = csize+1;
        if(handingSize <= this.totalSize){
            mDataList.add(t);
        }else{
            int extraCount = handingSize - this.totalSize;
            for(int i=0;i<extraCount;i++){
                mDataList.removeFirst();
            }
            mDataList.add(t);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public T getItem(int position) {
        return mDataList.get(position);
    }

}
