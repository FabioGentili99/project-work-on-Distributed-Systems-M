const AWS = require("aws-sdk")

const tableName = "service-registry";

class Injector {

    constructor(config_file){
        if (!Injector.instance){
            
            this.connection = new AWS.DynamoDB.DocumentClient();
            this.config_file = config_file;
            Injector._instance = this
            if (this.config_file) {
                try {
                    const fs = require('fs');
                    const xml2js = require('xml2js');
                    const parser = new xml2js.Parser();
    
                    const data = fs.readFileSync(this.config_file, 'utf8');
                    parser.parseString(data, (err, result) => {
                        if (err) {
                        console.error(err);
                        return;
                        }
    
                    const services = result.services.service;
                    services.forEach((service) => {
                        const name = service.name[0];
                        const address = service.address[0];
                        this.registerService(name, address);
                        });
                    });
                } catch (e) {
                    console.error(e);
                 }
            }

        }

        return Injector._instance
    }


        async registerService(serviceName, serviceUrl) {
            
            try{
                await this.connection.put(
                    {
                        TableName: tableName,
                        Item: {
                            ServiceName: serviceName,
                            ServiceAddress: serviceUrl,
                        }
                    }
                ).promise()
                } catch (error){
                    console.log(`errore during service registration: ${error}`)
                }

        }
    
        async getService(serviceName) {
           
            let result

            try{
                result = await this.connection.get(
                    {
                        TableName: tableName,
                        Key: {
                            ServiceName: serviceName
                        }
                    }
                ).promise()
            } catch (error){
                console.log(`errore during service retrieving: ${error}`)
            }
            return result.Item.ServiceAddress
        }

  
}

module.exports=Injector;
