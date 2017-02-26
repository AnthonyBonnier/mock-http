{
	"mock-http": [{
			"repeat": true,
			"input": "/rest/service/user",
			"output": {
				"mode": "sorted", // or random, sorted by default
				"content-type": "text/html", // text/html by default
				"responses": [{
						"status": 200, // 200 by default
						"times": 5, // 1 by default
						"output": "user1", // chosen output
					}, {
						"status": 404,
						"times": 1, // < 1 means forever at the end of previous responses (sorted mode only)
						"output": "User not found"
					}
				]
			}
		}, {
			"repeat": false,
			"input": "/rest/service/profile?title=#title#&date=#date#", // url params only in switch mode
			"output": {
				"mode": "switch", //repeat = false by default
				"content-type": "json/application",
				"responses": [{
						"status": 200,
						"case": [{ // Tabs of conditions to match
								"title": ["admin"],
								"date" : ["2017-01-01", "2017-01-02"]
							}
						],
						"output": "Admin Profil",
					}, {
						"status": 200,
						"case": [{
								"title": ["profile1", "profile2"]
							},
						],
						"output": "Profile found #title#", // write given param
					}, {
						"status": 404, 
						"default": true, // means other cases when none match
						"output": "Profile not found!",
					}
				]
			}
		}
	]
}
