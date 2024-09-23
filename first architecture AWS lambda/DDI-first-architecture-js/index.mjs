import Injector from "./Injector.js";


export const handler = async (event) => {
  const injector = new Injector();
  const helloService = await injector.getService("hello");
  


  await invokelambda (helloService.ServiceAddress)

  let response = {};
  response = {
    statusCode: 200,
    body: "execution completed with success",
  };

  return response;
};



async function invokelambda(address){
  const start = Date.now()
  await fetch(address, {method: 'GET'})
  const end = Date.now()

  console.log(`hello world function executed in ${end-start} ms `)
}
