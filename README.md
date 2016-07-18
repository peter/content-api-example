# Content API Example

This is an example Clojure app to showcase how to use the [content-api](https://github.com/peter/content-api)
library. The content-api library provides a CMS REST API based on MongoDB with user authentication, JSON schema validation, versioning, publishing, and relationships.

## Getting Started

First make sure you have [Leiningen/Clojure](http://leiningen.org) and Mongodb installed.

Get the source:

```bash
git clone git@github.com:peter/content-api-example.git
cd content-api-example
```

Create an admin user:

```
lein repl
(require '[app.content-api-example :as content-api-example])
(def system (content-api-example/-main :start-web false))
(require '[content-api.models.users :as users])
(users/create (:app system) {:name "Admin User" :email "admin@example.com" :password "admin"})
exit
```

Start the server:

```
lein run
```

In a different terminal, log in:

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"email": "admin@example.com", "password": "admin"}' http://localhost:5000/v1/login

export TOKEN=<token in header response above>
```

Basic CRUD workflow:

```bash
# create
curl -i -X POST -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -d '{"sections": {"title": {"se": "My Section"}, "slug": {"se": "my-section"}}}' http://localhost:5000/v1/sections

# get
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:5000/v1/sections/1

# list
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:5000/v1/sections

# update
curl -i -X PUT -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -d '{"sections": {"title": {"se": "My Section EDIT"}}}' http://localhost:5000/v1/sections/1

# delete
curl -i -X DELETE -H "Authorization: Bearer $TOKEN" http://localhost:5000/v1/sections/1
```

Now, let's look at versioning, associations, and publishing. Create two widgets and a page:

```bash
curl -i -X POST -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -d '{"widgets": {"title": {"se": "Latest Movies"}, "published_version": 1}}' http://localhost:5000/v1/widgets

curl -i -X POST -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -d '{"widgets": {"title": {"se": "Latest Series"}}}' http://localhost:5000/v1/widgets

curl -i -X POST -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -d '{"pages": {"title": {"se": "Start Page"}, "widgets_ids": [1, 2], "published_version": 1}}' http://localhost:5000/v1/pages
```

The first widget and the page are published since the `published_version` is set but the second widget is not. Now we can fetch the page with its associations:

```bash
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:5000/v1/pages/1?relationships=1
```

The response looks something like:

```json
{
  "data" : {
    "id" : "1",
    "type" : "pages",
    "attributes" : {
      "version" : 1,
      "created_at" : "2016-07-18T08:36:10.887+02:00",
      "type" : "pages",
      "id" : 1,
      "created_by" : "admin@example.com",
      "widgets_ids" : [ 1, 2 ],
      "title" : {
        "se" : "Start Page"
      },
      "published_version" : 1,
      "_id" : "578c78daf2b4a45bcddb65a1"
    },
    "relationships" : {
      "versions" : {
        "data" : [ {
          "id" : "1",
          "type" : "pages",
          "attributes" : {
            "created_by" : "admin@example.com",
            "created_at" : "2016-07-18T08:36:10.900+02:00",
            "version" : 1,
            "widgets_ids" : [ 1, 2 ],
            "id" : 1,
            "title" : {
              "se" : "Start Page"
            },
            "type" : "pages",
            "published_version" : 1,
            "_id" : "578c78daf2b4a45bcddb65a2"
          }
        } ]
      },
      "widgets" : {
        "data" : [ {
          "id" : "1",
          "type" : "widgets",
          "attributes" : {
            "version" : 1,
            "created_at" : "2016-07-18T08:35:02.281+02:00",
            "type" : "widgets",
            "id" : 1,
            "created_by" : "admin@example.com",
            "title" : {
              "se" : "Latest Movies"
            },
            "published_version" : 1,
            "_id" : "578c7896f2b4a45bcddb659b"
          }
        }, {
          "id" : "2",
          "type" : "widgets",
          "attributes" : {
            "version" : 1,
            "created_at" : "2016-07-18T08:35:31.708+02:00",
            "type" : "widgets",
            "id" : 2,
            "created_by" : "admin@example.com",
            "title" : {
              "se" : "Latest Series"
            },
            "_id" : "578c78b3f2b4a45bcddb659e"
          }
        } ]
      }
    }
  }
}
```

Notice how the page has a single version and how it is associated with two widgets, only the first of which has a published version.
Now, if we ask for the published version of the page (relevant to the end-user/public facing website) we don't get the version history
and we only get the first widget:

```bash
curl -i -H "Authorization: Bearer $TOKEN" 'http://localhost:5000/v1/pages/1?relationships=1&published=1'
```

If the page hadn't been published we would have gotten a 404.

In addition to the version history there is a `changelog` collection in Mongodb with a log of all write operations performed via the API.
Here is an example entry from the update above:

```json
{
  "_id": ObjectId("578c775ef2b4a45b712b493a"),
  "action": "update",
  "errors": null,
  "doc": {
    "_id": "578c774ef2b4a45b712b4936",
    "slug": {
      "se": "my-section"
    },
    "type": "sections",
    "title": {
      "se": "My Section EDIT"
    },
    "updated_at": ISODate("2016-07-18T06:29:50.142Z"),
    "id": 1,
    "updated_by": "admin@example.com",
    "version": NumberLong("2"),
    "created_by": "admin@example.com",
    "created_at": ISODate("2016-07-18T06:29:34.924Z")
  },
  "changes": {
    "title": {
      "from": {
        "se": "My Section"
      },
      "to": {
        "se": "My Section EDIT"
      }
    }
  },
  "created_by": "admin@example.com",
  "created_at": ISODate("2016-07-18T06:29:50.167Z")
}
```

## How Models Work

TODO: explain :type :schema :relationships :indexes :routes
