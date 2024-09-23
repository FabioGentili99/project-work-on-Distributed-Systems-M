const Injector = require("./Injector");
const { app } = require('@azure/functions');



app.http('DDI-first-architecture-js3', {
    methods: ['GET'],
    authLevel: 'anonymous',
    handler: async (request, context) => {
        const injector = new Injector();
        const result = await injector.getService("hello");
        const helloService = result.service
        var duration = result.duration
        context.log(`Read from CosmosDB table executed in ${duration} ms `)
        duration = await invokelambda (helloService.serviceAddress)

        context.log(`hello world function executed in ${duration} ms `)
        let response = {};
        response = {
            statusCode: 200,
            body: "execution completed with success",
        };

        return response;
    }
});


async function invokelambda(address){
    const start = Date.now()
    await fetch(address, {method: 'GET'})
    const end = Date.now()
    return end-start
  }