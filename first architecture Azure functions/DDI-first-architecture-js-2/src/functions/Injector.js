const {CosmosClient}  = require("@azure/cosmos");
const fs = require('fs');
const xml2js = require('xml2js');

const databaseId = "service-registry";
const containerId = "service-registry";
const key = "NwH78y4U7TNbh14kQOVTTPHvqctk2CGtdAdktHf9FqHYcRtSnbVf6kyWHnef70zWQXc73FGMOqsOACDbNYfMqA=="
const endpoint = "https://gheno.documents.azure.com:443/";
//const endpoint = "https://registry.documents.azure.com:443/"
//const key = "xegBV4RBjOaCvIUAKLeg1aBwL2kqQGtcJDH5HpMLfAJrgat8l4gnbUVkPg9ouwUv3ZxB7lL98lDIACDbejdGJg=="
//const client = new CosmosClient({ endpoint, auth: { masterKey: key } })
const client = new CosmosClient(process.env.CosmosDbConnectionSetting)



class Injector {
    constructor(config_file) {
        if (!Injector.instance) {
            //this.client = new CosmosClient({ endpoint, auth: { key }});
            //this.client = new CosmosClient(process.env.CosmosDbConnectionSetting)
            //this.database = this.client.database(databaseId);
            //this.container = this.database.container(containerId);
            this.client = client.database(databaseId).container(containerId)
            this.config_file = config_file;
            Injector.instance = this;

            if (this.config_file) {
                this.loadConfig();
            }
        }

        return Injector.instance;
    }

    async loadConfig() {
        try {
            const data = fs.readFileSync(this.config_file, 'utf8');
            const parser = new xml2js.Parser();

            parser.parseString(data, async (err, result) => {
                if (err) {
                    console.error(err);
                    return;
                }

                const services = result.services.service;
                for (const service of services) {
                    const name = service.name[0];
                    const address = service.address[0];
                    await this.registerService(name, address);
                }
            });
        } catch (e) {
            console.error(e);
        }
    }

    async registerService(serviceName, serviceUrl) {
        try {
            await this.client.items.create({
                id: serviceName,
                ServiceAddress: serviceUrl
            });
        } catch (error) {
            console.error(`Error during service registration: ${error}`);
        }
    }

    async getService(serviceName) {
        try {
            //const { resource } = await this.client.item( serviceName, "id").read();
            //return resource ? resource.ServiceAddress : null;
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
        }
            
    }
}

module.exports = Injector;