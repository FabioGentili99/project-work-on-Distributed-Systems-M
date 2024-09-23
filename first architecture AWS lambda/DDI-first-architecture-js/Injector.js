const AWS = require("aws-sdk")

const tableName = "service-registry2";

class Injector {

    constructor(){
        if (!Injector.instance){
            this.connection = new AWS.DynamoDB.DocumentClient();
            Injector._instance = this
        }
        return Injector._instance
    }


        async registerService(id, serviceName, serviceUrl) {
            
            try{
                await this.connection.put(
                    {
                        TableName: tableName,
                        Item: {
                            id: id,
                            ServiceName: serviceName,
                            ServiceAddress: serviceUrl,
                        }
                    }
                ).promise()
                } catch (error){
                    console.log(`error during service registration: ${error}`)
                }

        }
    
        async getService(id) {
           
            let result
            const start = Date.now()
            try{
                result = await this.connection.get(
                    {
                        TableName: tableName,
                        Key: {
                            id: id,
                        }
                    }
                ).promise()

                const end = Date.now()
                console.log(`Read from DynamoDB table executed in ${end-start} ms `)
                return result.Item
            } catch (error){
                console.log(`error during service retrieving: ${error}`)
            }
          
        }

  
}

module.exports=Injector;
