package com.example.myapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libcommon.EmptyView;
import com.example.myapplication.AbsViewModel;
import com.example.myapplication.R;
import com.example.myapplication.databinding.LayoutRefreshViewBinding;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbsListFragment<T , M extends AbsViewModel<T>> extends Fragment implements OnRefreshListener , OnLoadMoreListener {
    private LayoutRefreshViewBinding binding;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private EmptyView mEmptyView;
    private PagedListAdapter<T, RecyclerView.ViewHolder> adapter;
    protected M mViewModel;
    private DividerItemDecoration decoration;
    private String TAG = "AbsListFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutRefreshViewBinding.inflate(inflater, container,false);
        mRecyclerView = binding.recyclerView;
        mSmartRefreshLayout = binding.refreshLayout;
        mEmptyView = binding.emptyView;

        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);


        //PageListAdapter
        adapter = getAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setItemAnimator(null);

        decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);


        return binding.getRoot();
    }

    protected abstract void afterCreateView();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if(arguments.length>1){
            Log.d(TAG, "onViewCreated: "+"mViewModel");
            Type argument = arguments[1];
            Class modelClaz = ((Class) argument).asSubclass(AbsViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(modelClaz);

            mViewModel.getPageData().observe(this, new Observer<PagedList<T>>() {
                @Override
                public void onChanged(PagedList<T> pagedList) {
                    adapter.submitList(pagedList);
                }
            });

            mViewModel.getBoundaryPageData().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    finishRefresh(aBoolean);
                }
            });
            afterCreateView();


        }
    }

    public void submitList(PagedList<T> pagedList){
        if(pagedList.size()>0){
            adapter.submitList(pagedList);
        }
        finishRefresh(pagedList.size()>0);
    }

    public void finishRefresh(boolean hasData){
        PagedList<T> currentList = adapter.getCurrentList();
        hasData = hasData||currentList!=null && currentList.size()>0;

        RefreshState state = mSmartRefreshLayout.getState();
        if(state.isFooter&&state.isOpening){
            mSmartRefreshLayout.finishLoadMore();
        }else if(state.isHeader&&state.isOpening){
            mSmartRefreshLayout.finishRefresh();
        }

        if(hasData){
            mEmptyView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }
    public abstract PagedListAdapter<T,RecyclerView.ViewHolder> getAdapter();

}
