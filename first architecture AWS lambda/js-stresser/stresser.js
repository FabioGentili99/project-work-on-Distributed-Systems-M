const axios = require('axios');

const CONCURRENT_REQUESTS = 50; // Number of concurrent requests to send
const REQUESTS_PER_SECOND = 1; // Number of requests to send per second
const TOTAL_REQUESTS = 2; // Total number of requests to send per iteration

const js_function = 'https://0p1yt3o2ll.execute-api.eu-north-1.amazonaws.com/dev/getService'
const java_function = 'https://anhjt5148b.execute-api.eu-north-1.amazonaws.com/dev/getService'
const go_function = 'https://uy97e0i2c7.execute-api.eu-north-1.amazonaws.com/dev/getService'
const LAMBDA_ENDPOINT = js_function; // Replace with one of the above options


async function sendRequests() {
    console.log(`Sending ${TOTAL_REQUESTS} requests to ${LAMBDA_ENDPOINT}...`);

    const requests = [];
    for (let i = 0; i < TOTAL_REQUESTS; i++) {
        requests.push(axios.get(LAMBDA_ENDPOINT, { }));
    }

    const startTime = Date.now();

    try {
        await Promise.all(requests);
        const endTime = Date.now();
        const duration = endTime - startTime;
        const requestsPerSecond = TOTAL_REQUESTS / (duration / 1000);
        console.log(`All requests completed in ${duration}ms (${requestsPerSecond.toFixed(2)} requests per second).`);
    } catch (error) {
        console.error('Error:', error);
    }
}


async function main() {

    const delay1 = 1000 / REQUESTS_PER_SECOND;
    for (let i = 0; i < CONCURRENT_REQUESTS; i++) {
        setTimeout(sendRequests, i * delay1);
    }

    const delay2 = 1000 / (REQUESTS_PER_SECOND + 2)
    for (let i = 0; i < CONCURRENT_REQUESTS*2; i++) {
        setTimeout(sendRequests, i * delay2)
    }

    const delay3 = 1000 / (REQUESTS_PER_SECOND + 3)
    for (let i = 0; i < CONCURRENT_REQUESTS*3; i++) {
        setTimeout(sendRequests, i * delay3)
    }

    const delay4 = 1000 / (REQUESTS_PER_SECOND + 4)
    for (let i = 0; i < CONCURRENT_REQUESTS*3; i++) {
        setTimeout(sendRequests, i * delay4)
    }

    for (let i = 0; i < CONCURRENT_REQUESTS*3; i++) {
        setTimeout(sendRequests, i * delay4)
    }

    for (let i = 0; i < CONCURRENT_REQUESTS*2; i++) {
        setTimeout(sendRequests, i * delay4)
    }

    for (let i = 0; i < CONCURRENT_REQUESTS; i++) {
        setTimeout(sendRequests, i * delay1);
    }




}

main();
