import http from 'k6/http';

const BASE_URL = 'my-app:8080';

export const options = {
    scenarios: {
        steady_rpm: {
            executor: "constant-arrival-rate",
            rate: 100,
            timeUnit: "1m",
            duration: "5m",
            preAllocatedVUs: 5,
            maxVUs: 10,
        }
    }
}


export default function () {
    let data = {originURL: "https://google.com", "ttlMinutes": 100}
    let res = http.post(`http://${BASE_URL}/shorten-url`, JSON.stringify(data), {
        headers: {'Content-Type': 'application/json'},
    });
}