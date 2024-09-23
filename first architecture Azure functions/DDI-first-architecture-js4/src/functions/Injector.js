const {CosmosClient}  = require("@azure/cosmos");

const databaseId = "service-registry";
const containerId = "service-registry4";
const client = new CosmosClient(process.env.CosmosDbConnectionSetting)



class Injector {


    constructor() {
        if (!Injector.instance) {
            //this.client = new CosmosClient({ endpoint, auth: { key }});
            //this.client = new CosmosClient(process.env.CosmosDbConnectionSetting)
            //this.database = this.client.database(databaseId);
            //this.container = this.database.container(containerId);
            this.container = client.database(databaseId).container(containerId)
            Injector.instance = this;
        }

        return Injector.instance;
    }


    async registerService(id, serviceName, serviceUrl) {
        try {
            await this.container.items.create({
                id: id,
                ServiceName: serviceName,
                ServiceAddress: serviceUrl
            });
        } catch (error) {
            console.error(`Error during service registration: ${error}`);
        }
    }

    async getService(serviceId) {
        /*try{
            const querySpec = {
                query: "SELECT * FROM c WHERE c.id = @id",
                parameters: [
                    {
                        name: "@id",
                        value: serviceName
                    }
                ]
            };
    
            const { resources: items } = await this.client.items.query(querySpec).fetchAll();
            console.log("item: " + items)
            if (items.length > 0) {
                return items[0];
            } else {
                return null;
            }
            


        } catch (error) {
            console.error(`Error during service retrieval: ${error}`);
        }*/
        
        const start = Date.now()
        const service = (await this.container.item(serviceId, serviceId).read())
        const end = Date.now()
        console.log(`Read from CosmosDB table executed in ${end-start} ms `)
        return {service: service.resource, duration: end-start}
        

    }
}

module.exports = Injector;