{
	"mock-http": [{
			"repeat": true,
			"input": "/rest/service/user",
			"output": {
				"mode": "sorted",
				"content-type": "text/html",
				"responses": [{
						"status": 200, // 200 by default
						"times": 5, // 1 by default
						"output": "user1", // chosen output
					}, {
						"status": 404,
						"times": 1,
						"output": "User not found"
					}
				]
			}
		}, {
			"repeat": false,
			"input": "/rest/service/profile?title=#title#",
			"output": {
				"mode": "switch", //repeat = false
				"content-type": "json/application",
				"responses": [{
						"status": 200,
						"params": [{
								"title": ["admin"]
							}
						],
						"output": "Admin Profil",
					}, {
						"status": 200,
						"params": [{
								"title": ["profile1", "profile2"]
							},
						],
						"output": "Profile found #param#2",
					}
				]
			}
		}
	]
}
