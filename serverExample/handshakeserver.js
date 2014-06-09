var net = require('net');

var i = 0;



Math.randInt = function (min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
};


var request_format = [
    /SYN\s+(\d+(?:.\d+){3})\s+(\w+)\s+(\d+)/,
    /SYN-ACK\s+(\d+(?:.\d+){3})\s+(\w+)\s+(\d+)\s+(\d+)/
];

/*
"SYN 0.0.0.0 KIM 897134"
"SYN-ACK 1.1.1.1 SERVER 897135 89465"
"SYN-ACK 0.0.0.0 KIM 897136 89466"
"ACK 1.1.1.1 SERVER 897137 89467"
*/


var server = net.createServer(function (conn) {
    var id = i + 1;
    i += 1;

    var serverlog = function () {
        var args = Array.prototype.slice.call(arguments);
        args.unshift(new Date());
        args.unshift(id);
        console.log.apply(console, args);
    };

    serverlog("Connection from " + conn.remoteAddress + " on port " + conn.remotePort);
    conn.setEncoding("utf8");

    var line = "";
    var state = 0;
    var A;
    var B = Math.randInt(100000, 1000000);
    var requester = "";
    conn.on("data", function (data) {
        try {
            for (var i = 0; i <= data.length; i++) {
                var response = "";
                var c = data.charAt(i);
                line += c;
                if (c == '\n') {
                    line = line.trim();
                    serverlog("Received Message: " + line);

                    if (state > 1) {
                        serverlog("Ending connection from " + conn.remoteAddress + " on port " + conn.remotePort);
                        conn.end();
                        return;
                    }

                    try {
                        var request = line.match(request_format[state]);
//                        serverlog("cjl:request = " + request); //cjl
                        if (request) {
                            // make sure the remote address matches the requset
                            var req_ip = request[1];
//                            serverlog("cjl:req_ip = " + req_ip); //cjl
                            var req_name = request[2];
//                            serverlog("cjl:req_name = " + req_name); //cjl
                            var req_A = parseInt(request[3], 10);
//                            serverlog("cjl:req_A = " + req_A); //cjl
                            var req_B = parseInt(request[4], 10);
//                            serverlog("cjl:req_B = " + req_B); //cjl
                            
                            if (req_ip != conn.remoteAddress) {
                                throw {"msg": "Bad Request. IP Mismatch"};
                            }

                            if (state == 0) {
                                requester = req_name;
                                A = 1 + req_A;
                                response = "SYN-ACK " + conn.localAddress + " SERVER " + A + " " + B + "\r\n";
                                state += 1;
                            } else {
                                serverlog("cjl:handle rtnHndshk");
                                A += 1;
                                B += 1;
                                if (requester != req_name) {
                                    throw {"msg": "Bad Request. Request Name Mismatch"};
                                }
                                if (req_A != A || req_B != B) {
                                    throw {"msg": "Bad Request. Sequence Mismatch."};
                                }
                                A += 1;
                                B += 1;
                                response = "ACK " + conn.localAddress + " SERVER " + A + " " + B + "\r\n";
                                state += 1;
                            }
                            serverlog("cjl:response = " + response); //cjl
                            conn.write(response);
                        } else {
                            throw {"msg": "Bad Request"};
                        }
                        line = "";
                    } catch(e) {
                        serverlog(e.msg + " - Terminating Connection.");
                        conn.write(e.msg + "\n");
                        conn.end();
                    }
                }
            }
        } catch (e) {
            serverlog(e.msg + " - Terminating Connection.");
            conn.end();
        }
    });
});


server.listen(2000)
