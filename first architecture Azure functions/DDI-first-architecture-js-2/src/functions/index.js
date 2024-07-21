const Injector = require("./Injector");
const { app } = require('@azure/functions');
const injector = new Injector('services.xml');


app.http('DDI-first-architecture-js', {
    methods: ['GET'],
    authLevel: 'anonymous',
    route: 'getService',
    handler: async (context) => {
        //const injector = new Injector('services.xml');
        const serviceAddress = await injector.getService("test");
        //context.info(`address: "${JSON.stringify(serviceAddress)}"`)
        let response = {};
        if (serviceAddress != null) {
            response = {
            statusCode: 200,
            body: JSON.stringify(serviceAddress.ServiceAddress),
            };
        }
        else {
            response = {
            statusCode: 404,
            body: JSON.stringify('service not found')
        };
        }

        //context.res = response;
        return response;
    }
});
