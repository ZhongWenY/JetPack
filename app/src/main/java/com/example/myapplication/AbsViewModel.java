package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class AbsViewModel<T> extends ViewModel {

    private DataSource dataSource;

    private LiveData<PagedList<T>> pageData;

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    protected PagedList.Config config;
    public AbsViewModel(){
        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
                .build();

        pageData=new LivePagedListBuilder(factory,config)
                .setInitialLoadKey(0)
                .setBoundaryCallback(callback)
                .build();
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public LiveData<PagedList<T>> getPageData(){
        return pageData;
    }

    public DataSource getDataSource(){
        return dataSource;
    }

    PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
        }
    };

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            dataSource = createDataSource();
            return dataSource;
        }
    };

    public abstract DataSource createDataSource();
}
