package com.android.getit.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.getit.R;
import com.android.getit.dataRequestResult.OrderListResult;

/**
 * Created by sminger on 2015/12/5.
 */
public class FragmentOrder extends BaseFragment {
    private OrderAdapter mOrderAdapter;
    private ListView mOrderList;
    private String fakeData = " {\"OrderName\":\"订单名称\",\"OrderTime\":\"2015\\/12\\/5 13:00:28\",\"Services\":[{\"ServiceCount\":1,\"ServiceDescription\":\"只是安装服务，不提供安装盘\",\"ServiceID\":1,\"ServiceTypeID\":1,\"ServiceTypeName\":\"自营\"},{\"ServiceCount\":2,\"ServiceDescription\":\"提供全套配置服务\",\"ServiceID\":2,\"ServiceTypeID\":1,\"ServiceTypeName\":\"自营\"},{\"ServiceCount\":3,\"ServiceDescription\":\"自带安装程序\",\"ServiceID\":3,\"ServiceTypeID\":2,\"ServiceTypeName\":\"第三方\"}],\"TotalPrice\":370,\"UserID\":2,\"UserName\":\"wang\"}";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_list, container, false);
        mOrderList = (ListView)rootView.findViewById(R.id.orderList);
        mOrderAdapter = new OrderAdapter();
        getOrderList();
        mOrderList.setAdapter(mOrderAdapter);

        return rootView;
    }

    private void getOrderList(){
        mOrderAdapter.setData(JSON.parseObject(fakeData, OrderListResult.class));
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    private class OrderAdapter extends BaseAdapter{

        private LayoutInflater mInflater = LayoutInflater.from(getActivity());
        private OrderListResult mOrderlistResult = null;

        public void setData(OrderListResult orderListResult) {
            mOrderlistResult = orderListResult;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mOrderlistResult.Services.size();
        }

        @Override
        public Object getItem(int position) {
            mOrderlistResult.Services.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.order_item, parent, false);
                holder = new ViewHolder();
                initViewHolder(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            initViewData(holder, position);
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public void initViewHolder(ViewHolder viewHolder, View view) {
            viewHolder.ServiceCount = (TextView)view.findViewById(R.id.ServiceCount);
            viewHolder.ServiceDescription = (TextView)view.findViewById(R.id.ServiceDescription);
            viewHolder.ServiceID = (TextView)view.findViewById(R.id.ServiceID);
            viewHolder.ServiceTypeID = (TextView)view.findViewById(R.id.ServiceTypeID);
            viewHolder.ServiceTypeName = (TextView)view.findViewById(R.id.ServiceTypeName);

        }

        public void initViewData(ViewHolder viewHolder, int position) {
            viewHolder.ServiceCount.setText("ServiceCount:" + mOrderlistResult.Services.get(position).ServiceCount);
            viewHolder.ServiceTypeName.setText("ServiceTypeName:" + mOrderlistResult.Services.get(position).ServiceTypeName);
            viewHolder.ServiceDescription.setText("ServiceDescription:" + mOrderlistResult.Services.get(position).ServiceDescription);
            viewHolder.ServiceID.setText("ServiceID:" + mOrderlistResult.Services.get(position).ServiceID);
            viewHolder.ServiceTypeID.setText("ServiceTypeID:" + mOrderlistResult.Services.get(position).ServiceTypeID);
        }

        public class ViewHolder{
            TextView ServiceCount;
            TextView ServiceDescription;
            TextView ServiceID;
            TextView ServiceTypeID;
            TextView ServiceTypeName;
        }
    }
}
