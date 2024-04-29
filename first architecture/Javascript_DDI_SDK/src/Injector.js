class Injector {


  constructor(config_file) {
    this.registryBaseUrl = 'http://localhost:8080';
    this.config_file = config_file;

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

    async registerService(serviceName, serviceUrl) {
    const endpoint = `${this.registryBaseUrl}/register/${serviceName}`;
    try {
        const response = await fetch(endpoint,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'text/plain'
                    },
                    body: serviceUrl
                }
        );
        if (response.ok) {
            console.log(`Service ${serviceName} registered successfully.`);
        } else {
            console.error(`Failed to register service ${serviceName}: ${response.statusText}`);
        }
    }catch (error) {
        console.error(`Failed to register service ${serviceName}: ${error.message}`);
    }
  }

  async getService(serviceName) {
    const endpoint = `${this.registryBaseUrl}/discover/${serviceName}`;
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const serviceUrl = await response.text();
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
  }

  
}

module.exports = Injector;
