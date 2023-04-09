# Read Instruction.txt File.....!!!!!
---
install python3\
install pip3

Then go to the path **/usr/local/bin**\
sudo pip3 install mongo-connector\
pip3 install elastic2-doc-manager\
pip3 install -U elasticsearch

sudo rm /var/lib/mongodb/mongod.lock\
sudo service mongod start


# _Standalone to replicaset_
Mongo ReplicaSet

mongod --dbpath /data/db --repair


Installation :
sudo apt install mongodb-org

Config file : /etc/mongod.conf
 
# mongod.conf

 for documentation of all options, see:\
   http://docs.mongodb.org/manual/reference/configuration-options/

---

replication:\
   replSetName: rs0\

setParameter:\
   enableLocalhostAuthBypass: false

---
Once all done.

>mongo
>> rs.initiate()

>> rs.status()

# Service 
--- 

cat oauth-mongoconnector.service 

[Unit]\
Description=Mongo Connector - Oauth\

[Service]\
Type=simple\
ExecStart=/usr/local/bin/mongo-connector -c /etc/conf/gaian/mongo-connector/oauth/oauth-config.json\
#Restart=always\
User=gaian\
Restart=on-failure


[Install]\
WantedBy=multi-user.target

---

# Config File

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
