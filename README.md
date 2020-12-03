# build

## important note
As tests use testcontainers, the CI-agents need to be able to start containers using docker
This way I get best coverage with the least tests under almost prod conditions. 
It also makes TDD easier, as I just need to know the api and response model in advance and prevents duplicate tests.

I did not define resource limits on the deployment pods - in production this should be done to enable k8s auto scaling features.
    
## building and running
    make build
    mvn[w] package
    
also builds and tags the docker image

When testing with local installatiosn like minikube, set the registries for pushing and pulling images to use the local registry

In minikube there is a prepared command to get the necessary ENV variables
    ```eval $(minikube docker-env); make build;```

# deployment

The deployment is split into the smallest parts to make the files more readable, in a real world scenario, 
they would be combined with kustomize or helm charts.

For this exercise and POC setup, I choose to use host-path directories for persistent volume backing - this makes no sense in production, 
but is the easiest solution to get the app running quickly on local single node clusters like minikube or docker for desktop.

I created a storage class, so switching this to make it actually production ready would be as simple as changing the storage class definition.

# Spec:

# accounts

## /accounts

A simple CRUD API with the api user only needing to know the **username**!

    GET(/{id})
    POST
    PUT(/{id})
    DELETE(/{id})

## /statistics

returns statistics for the account (events per day and type)

    GET(/statistics/account/{id})
    
```json
    [
      {
        "date": "yyyy-MM-dd",
        "events": [
          {
            "name": "created",
            "count": 1000
          }, [...]
        ]
      }, [...] 
    ]
```
 
###model

```json
{
    "id": "generated",
    "name": "unique string"
}
```

##/events

        GET(/{id}), 
        POST

**Events should be deleted after 30 days, but stay in the statistics**

###model

```json
{
    "id": "generated",
    "happenedAt":"yyyy-MM-dd hh:mm:ss",
    "type": "string"
}
```


# performance considerations

100 accounts with 1000 events per account per day

```100.000 requests per day```

###avg 
````~ 70 req/minute === ~ 1 req/s````

###peaks

assume avg * 2 = peak:
```~ 140 req/minute === ~ 2 req/s```

###conclusion

No significant load on any system - standard tools are able to handle this easily.

# hypothetical future load

assuming 100 times the current estimation for load

###avg 
```~ 7000 req/minute === ~ 100 req/s```

###peaks

assume avg * 2 = peak:
```~ 14.000 req/minute === ~ 200 req/s
```
###conclusion

As there are no time/compute intensive tasks to do for handling a request, this will also not overwhelm any standard solutions.
If the load is expected to multiply by 10 again, mysql would probably have to be exchanged for a storage solution that can more easily be clustered
(eg.: mongo, cassandra)

The app itself is stateless and connects to the storage via a k8s service, so the real issue would be horizontally scaling storage.

# architectural considerations

1) This is an exercise - so we should not consider any possible future development over the given increase in load. (KISS)
2) most of the load will come from processing events and serving statistics, so keep the other part's footprint small. (KISS)
3) recalculating statistics is expensive - store them ahead of time

# technical decisions

## my decision criteria

1. does the performance suffice? 
2. how easy is it to find someone to maintain the solution.
3. how well would theoretical future increases in load be dealt with (scaling beyond the requirements)
4. how easy is migrating from the solution to another one

## mysql (or any rel. db)

The amount of data to be stored is low, events even get deleted after 30 days - there is no need for a fancy solution here.
Every developer can understand and work with JPA+mysql, so if the performance is adequate, I'd always choose the solution that
requires the least 'uncommon' skills to maintain.

##### alternatives

If this app was 100% ensured to run on GCP, I would have choosen Firestore. As the requirement is 'any k8s cluster', I didn't.
Other key value stores (eg. redis would work here too, with the added benefit of automatic key expiry and ease of scaling), but
as stated, my priorities are:



rdbms beats any solution in the second consideration. Finding devs is more expensive than switching storage later on.


## influx

As 'deleting events after 30 days, without loosing statistics info' is a requirement, the statistics need to be stored separately.
A time series database is purpose built for storing and querying this kind of data.

I choose influx because 

1. I am familiar with the java client
2. the sql like query language
3. I like things built in go

##### alternatives

I could have stored this info in mysql too, but this kind of data gets out of hand very quickly and mysql won't be able to handle the load for long.
Influx is purpose built and easier to cluster.

Another good alternative would have been to use redis atomic counters - but this would have been quite esoteric and violate point 2. of my decision criteria.

Also migrating from influx to eg. gcp ``` bigtable / bigquery``` is easier than migrating from a redis counter solution.
