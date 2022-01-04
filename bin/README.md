# Mongodb-ElasticSearch-MongoConnector
install python3
install pip3

then go to the path /usr/local/bin
sudo pip3 install mongo-connector
and pip3 install elastic2-doc-manager
pip3 install -U elasticsearch

sudo rm /var/lib/mongodb/mongod.lock
sudo service mongod start


Standalone to replicaset
Mongo ReplicaSet

mongod --dbpath /data/db --repair


Installation :
sudo apt install mongodb-org

Config file : /etc/mongod.conf
# mongod.conf

# for documentation of all options, see:
#   http://docs.mongodb.org/manual/reference/configuration-options/

# Where and how to store data.
storage:
  dbPath: /var/lib/mongodb
  journal:
    enabled: true
#  engine:
#  mmapv1:
#  wiredTiger:

# where to write logging data.
systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log

# network interfaces
net:
  port: 27017
  bindIp: 127.0.0.1


# how the process runs
processManagement:
  timeZoneInfo: /usr/share/zoneinfo
#  fork : true

#security:
#  keyFile: /opt/mongo/mongo-keyfile

#operationProfiling:

replication:
   replSetName: rs0
#sharding:

## Enterprise-Only Options:

#auditLog:

#snmp:
setParameter:
   enableLocalhostAuthBypass: false


Once all done.

mongo
> rs.initiate()

> rs.status()



Service 
 cat oauth-mongoconnector.service 
[Unit]
Description=Mongo Connector - Oauth

[Service]
Type=simple
ExecStart=/usr/local/bin/mongo-connector -c /etc/conf/gaian/mongo-connector/oauth/oauth-config.json
#Restart=always
User=gaian
Restart=on-failure


[Install]
WantedBy=multi-user.target


Config File
{
	"mainAddress": "mongodb://localhost:27017/oauth?maxIdleTimeMS=0&socketTimeoutMS=0",
	"oplogFile": "/var/log/gaian/mongo-connector/oauth/oauth-oplog.timestamp",
	"noDump": false,
	"batchSize": -1,
	"verbosity": 1,
	"continoueOnError": true,

	"logging": {
        	"type": "file",
        	"filename": "/var/log/gaian/mongo-connector/oauth/oauth-mongo-connector.log",
		"format": "%(asctime)s [%(levelname)s] Oauth %(name)s:%(lineno)d - %(message)s"
	},

	"namespaces": {
		"oauth.customer": {
       			"rename": "customer.instance",
              		"includeFields": [
        			"_id",
        			"email",
        			"age",
                		"cardDetails",
        			"firstName"
      			]
    		}
	},


	"docManagers": [
        {
            	"docManager": "elastic2_doc_manager",
   		"targetURL": "http://localhost:9200/",
            	"args": {
              		"clientOptions": {
                		"timeout": 1000
              		}
        	},
            	"bulkSize": 10
		
        }
    	]
}
