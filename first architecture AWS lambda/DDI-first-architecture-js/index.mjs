//const Injector = require("./Injector.js")
import Injector from "./Injector.js";

export const handler = async (event) => {
  //const Injector = require("./Injector");
  const injector = new Injector('services.xml');
  const serviceAddress = injector.getService("test");
  let response = {};
  if (serviceAddress != null) {
    response = {
    statusCode: 200,
    body: JSON.stringify(serviceAddress),
    };
  }
  else {
    response = {
      statusCode: 404,
      body: JSON.stringify('service not found')
    };
  }
  return response;
};
