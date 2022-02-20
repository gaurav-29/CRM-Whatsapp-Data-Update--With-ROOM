package com.oceanmtech.crmwhatsappdataupdate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.oceanmtech.crmwhatsappdataupdate.Database.DataTable;
import com.oceanmtech.crmwhatsappdataupdate.Database.MyDatabase;
import com.oceanmtech.crmwhatsappdataupdate.databinding.RowCrmdataBinding;

import java.util.List;

public class CRMAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RowCrmdataBinding mBinding;
    private final List<DataTable> crmList;
    private final Context ctx;
    MyDatabase myDatabase;

    public CRMAdapter(List<DataTable> crmList, MainActivity mContext) {
        setHasStableIds(true);
        this.crmList = crmList;
        Log.d("TEST", String.valueOf(crmList));
        this.ctx = mContext;

        myDatabase = Room.databaseBuilder(mContext, MyDatabase.class, "crmdata.db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.row_crmdata, parent, false);
        return new CRMAdapter.ItemViewHolder(mBinding.getRoot(), mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        DataTable currentData = crmList.get(position);

        if(currentData.getMobile().equalsIgnoreCase("")) {
            mBinding.cvNumName.setVisibility(View.GONE);
            mBinding.tvMessage.setText(currentData.getMessage());
        }
        if(currentData.getMobile().length()!=10) {
            mBinding.cvNumName.setVisibility(View.GONE);
            mBinding.tvMessage.setText((currentData.getMessage()));
        }
        if(currentData.getName().equalsIgnoreCase("") && currentData.getMobile().length()>0){
            mBinding.tvName.setVisibility(View.GONE);
            mBinding.tvMobile.setText(currentData.getMobile());
            mBinding.tvMessage.setText((currentData.getMessage()));
        }
        if(currentData.getMobile().length()>0 && currentData.getName().length()>0 && myDatabase.dao().checkNumber(currentData.getMobile()).size()>0){
            mBinding.tvName.setText(currentData.getName());
            mBinding.tvMobile.setText(currentData.getMobile());
            mBinding.tvMessage.setText((currentData.getMessage()));
        }
    }

    @Override
    public int getItemCount() {
        return crmList.size();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowCrmdataBinding mBinding;

        public ItemViewHolder(View view, RowCrmdataBinding mBinding) {
            super(view);
            this.mBinding = mBinding;
        }
    }
}
