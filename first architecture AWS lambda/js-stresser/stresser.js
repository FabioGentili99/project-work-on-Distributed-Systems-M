const axios = require('axios');

const CONCURRENT_REQUESTS = 50; // Number of concurrent requests to send
const REQUESTS_PER_SECOND = 1; // Number of requests to send per second
const TOTAL_REQUESTS = 2; // Total number of requests to send per iteration

const js_function = 'https://y564h9zawb.execute-api.eu-north-1.amazonaws.com/dev/getService?serviceName=test'
const java_function = 'https://k521q8ci2k.execute-api.eu-north-1.amazonaws.com/dev/getService'
const go_function = 'https://houakeimrh.execute-api.eu-north-1.amazonaws.com/dev/getService'
const LAMBDA_ENDPOINT = java_function; // Replace with one of the above options


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

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function main() {

    const delay1 = 1000 / REQUESTS_PER_SECOND;
    for (let i = 0; i < CONCURRENT_REQUESTS; i++) {
        setTimeout(sendRequests, i * delay1);
    }

    await sleep(1000 * 60 * 6)

    const delay2 = 1000 / (REQUESTS_PER_SECOND + 2)
    for (let i = 0; i < CONCURRENT_REQUESTS*2; i++) {
        setTimeout(sendRequests, i * delay2)
    }

    await sleep(1000 * 60 * 6)

    const delay3 = 1000 / (REQUESTS_PER_SECOND + 3)
    for (let i = 0; i < CONCURRENT_REQUESTS*3; i++) {
        setTimeout(sendRequests, i * delay3)
    }

    await sleep(1000 * 60 * 6)

    const delay4 = 1000 / (REQUESTS_PER_SECOND + 4)
    for (let i = 0; i < CONCURRENT_REQUESTS*4; i++) {
        setTimeout(sendRequests, i * delay4)
    }


}

main();
