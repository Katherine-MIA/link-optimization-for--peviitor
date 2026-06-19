package org.example.persistence;

import java.util.List;
import java.util.Optional;

/*
    The methods described by the interface are subject to changes based on performance metrics.
    E.g. Batch sizes might vary in order to accommodate running environment;

    Not mandatory: Suggested two versions for some actions, one for the raw link variant (String)
    and one for the entity class. One implementation is sufficient if the other is deemed to be
    unnecessary.

    T - PersistedLink.class; E - LinkStatus.class;

    ** Auxiliary todo: change schema for persisted_links table to include the 'host' column and
                    make the respective changes to the PersistedLink entity class.

    The provided guidelines are orientative and can be disregarded in favor of better/more efficient
    strategies for database communication.

    If possible, for all methods that return lists of entities, there should be a 'nextBatch' mechanism
    as the entities must be processed in batches for memory and performance management.
 */
public interface LinkRepository<T,E> {
    /*
        Should send the PersistedLink instance to be inserted in the db.
     */
    void addPersistedLink(T link);
    /*
        Should send a batch of PersistedLink instances to be inserted into db.
        The method should either specify batch size limitation or create its own
        logic for splitting payload into multiple requests.
     */
    void addPersistedLinks(List<T> links);
    /*
        Optional: Possibility to have an object created and stored by providing just the link.
        When status is not specified it should be considered NEW, unless entity already exists.
        Signature can be changed for link parameter String type to HttpUrl for easy extraction of host.
        Return type can be changed to Optional<List<T>>, if more convenient/optimal.
     */
    void addNewLink(String link, Optional<E> status);
    /*
        Optional: Possibility for entity objects to be created and persisted using only links.
        Just like the addPersistedLinks() method, the method should provide a size requirement
        or should handle the request optimization.
        Return type can be changed to Optional<List<T>>, if more convenient/optimal.
     */
    void addNewLinks(List<String> links, Optional<List<E>> statuses);
    /*
        Optional: The purpose of this functionality is to be used in checking for db duplicates or
        checking the status and other attributes for the fetched entity.
     */
    Optional<T> searchForLink(String link);
    /*
        Persisting the changes made on a PersistedLink entity object, can be combined with
        add/create method into an upsert one if more convenient.
     */
    void updatePersistedLink(T link);
    /*
        Delete entity associated with provided link.
     */
    void deleteLink(String link);
    /*
        Remove 'link' entity from db.
        Same as the above method, therefore one of these implementations is not required.
     */
    void deletePersistedLink(T link);
    /*
        This method should be used to initiate the process of removing database entities that have
        a 'INACTIVE'/'INVALID' value for 'status'.
     */
    void deleteMarkedLinks();
    /*
        This method should return a list of PersistedLink entities selected by host value, having a
        fixed size optimized for memory management and request payload size.
        As multiple threads should access this method, the result will be the system having to manage
        in memory, all fetched entities for the duration of their processing.
     */
    Optional<List<T>> getLinksByHost(String host);
    /*
        This method should return a list of PersistedLink entities selected by status, having a
        fixed size. All constraints regarding optimization for bulk fetching apply.
     */
    Optional<List<T>> getLinksByStatus(E e);
    /*
        Optional: This method should return all db entities in batches of fixed size.
        Method name can be changed to be more suggestive of the behaviour, as well as any other necessary
        changes or method additions.
     */
    Optional<List<T>> getAllLinks();
    /*
        This method should return a list of all distinct host values stored.
        This method will be used for a manageable amount of stored hosts that can
        be requested once.
     */
    Optional<List<String>> getHosts();
    /*
        This is a guideline method for the case when the number of distinct hosts stored, exceeds
        a manageable amount for processing and/or requesting once.
        The implementation, method signature and general logic can be adapted, refactored and optimized
        as necessary.
     */
    Optional<List<String>> getHostsBatch();

}
