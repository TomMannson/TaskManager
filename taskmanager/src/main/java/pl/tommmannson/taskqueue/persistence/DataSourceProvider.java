package pl.tommmannson.taskqueue.persistence;

/**
 * Created by tomasz.krol on 2017-03-15.
 */

public interface DataSourceProvider<T extends Serializer> {

    public void configureSerializer(T serializer);
}
