class Injector {


    constructor(config_file) {
      if (!Injector._instance){
          this.registry = new Map()
          //this.registryBaseUrl = 'https://kcnbtb47t0.execute-api.eu-north-1.amazonaws.com/dev';
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
        /*const endpoint = `${this.registryBaseUrl}/register`;
        try {
            const response = await fetch(endpoint,
                    {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: {'serviceName':serviceName , 'serviceAddress':serviceUrl}
                    }
            );
            if (response.ok) {
                console.log(`Service ${serviceName} registered successfully.`);
            } else {
                console.error(`Failed to register service ${serviceName}: ${response.statusText}`);
            }
        }catch (error) {
            console.error(`Failed to register service ${serviceName}: ${error.message}`);
        }*/
            this.registry.set()
            console.log(`Service ${serviceName} registered successfully.`)
        }
    
      async getService(serviceName) {
        /*const endpoint = `${this.registryBaseUrl}/lookup?serviceName=${serviceName}`;
        try {
            const response = await fetch(endpoint);
            if (response.ok) {
                            const serviceUrl = await response.text();
            if (serviceUrl === "null"){
              return null
            }
            console.log(`Discovered service ${serviceName} at ${serviceUrl}`);
            return serviceUrl;
        } else {
            console.error(`Failed to discover service ${serviceName}: ${response.statusText}`);
            return null;
        }
    } catch (error) {
        console.error(`Failed to discover service ${serviceName}: ${error.message}`);
        return null;
    }
    */
    const response = this.registry.get(serviceName)
    if (response != null){
        return response
    } else {
        console.error(`Failed to discover service ${serviceName}`)
        return null
    }
    
  }

  
}

module.exports=Injector;
