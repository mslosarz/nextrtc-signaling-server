/**
 * This library require https://webrtc.github.io/samples/src/js/adapter.js
 */
// 'use strict';

function Message(signal, to, content, custom) {
	this.signal = signal;
	this.to = to;
	this.content = content;
	this.custom = custom;
};

function NextRTC(config) {

	if (NextRTC.instance == null) {
		NextRTC.instance = this;
	} else {
		return NextRTC.instance;
	}

	this.mediaConfig = config.mediaConfig !== undefined ? config.mediaConfig : null;
	this.type = config.type;

	this.signaling = new WebSocket(config.wsURL);
	this.peerConnections = {};
	this.localStream = null;
	this.signals = {};

	this.on = function(signal, operation) {
		this.signals[signal] = operation;
	};

	this.call = function(event, data) {
		for ( var signal in this.signals) {
			if (event === signal) {
				return this.signals[event](this, data);
			}
		}
		console.log('Event ' + event + ' do not have defined function');
	};

	this.join = function(convId) {
		var nextRTC = this;
		navigator.mediaDevices.getUserMedia(nextRTC.mediaConfig).then(function(stream) {
			nextRTC.localStream = stream;
			nextRTC.call('localStream', {
				stream : stream
			});
			nextRTC.request('join', null, convId);
		}, error);
	};

	this.create = function(convId, custom) {
		var nextRTC = this;
		navigator.mediaDevices.getUserMedia(nextRTC.mediaConfig).then(function(stream) {
			nextRTC.localStream = stream;
			nextRTC.call('localStream', {
				stream : stream
			});
			nextRTC.request('create', null, convId, custom);
		}, error);
	};

	this.request = function(signal, to, convId, custom) {
		var req = JSON.stringify(new Message(signal, to, convId, custom));
		console.log("res: " + req);
		this.signaling.send(req);
	};

	this.signaling.onmessage = function(event) {
		console.log("req: " + event.data);
		var signal = JSON.parse(event.data);
		NextRTC.instance.call(signal.signal, signal);
	};

	this.signaling.onclose = function(event) {
		NextRTC.instance.call('close', event);
	};

	this.signaling.onerror = function(event) {
		NextRTC.instance.call('error', event);
	};

	this.preparePeerConnection = function(nextRTC, member) {
		if (nextRTC.peerConnections[member] == undefined) {
			var pc = new RTCPeerConnection(config.peerConfig);
			pc.onaddstream = function(evt) {
				nextRTC.call('remoteStream', {
					member : member,
					stream : evt.stream
				});
			};
			pc.onicecandidate = function(evt) {
				handle(pc, evt);

				function handle(pc, evt){
					if((pc.signalingState || pc.readyState) == 'stable'
						&& nextRTC.peerConnections[member]['rem'] == true){
						nextRTC.onIceCandidate(nextRTC, member, evt);
						return;
					}
					setTimeout(function(){ handle(pc, evt); }, 2000);
				}
			};
			nextRTC.peerConnections[member] = {}
			nextRTC.peerConnections[member]['pc'] = pc;
			nextRTC.peerConnections[member]['rem'] = false;
		}
		return nextRTC.peerConnections[member];
	};

	this.offerRequest = function(nextRTC, from) {
		nextRTC.offerResponse(nextRTC, from);
	};

	this.offerResponse = function(nextRTC, signal) {
	    var pc = nextRTC.preparePeerConnection(nextRTC, signal.from);
        		pc['pc'].addStream(nextRTC.localStream);
        pc['pc'].createOffer({offerToReceiveAudio: 1, offerToReceiveVideo: 1})
        .then(function(desc) {
            pc['pc'].setLocalDescription(desc)
                .then(function() {
                    nextRTC.request('offerResponse', signal.from, desc.sdp);
                }, error);
        });
	};

	this.answerRequest = function(nextRTC, signal) {
		nextRTC.answerResponse(nextRTC, signal);
	};

	this.answerResponse = function(nextRTC, signal) {
		var pc = nextRTC.preparePeerConnection(nextRTC, signal.from);
		pc['pc'].addStream(nextRTC.localStream);
		pc['pc'].setRemoteDescription(new RTCSessionDescription({
			type : 'offer',
			sdp : signal.content
		})).then(function() {
    		pc['rem'] = true;
    		pc['pc'].createAnswer().then(function(desc) {
    		    pc['pc'].setLocalDescription(desc).then(function() {
        		    nextRTC.request('answerResponse', signal.from, desc.sdp);
        	    });
            });
        });
	};

	this.finalize = function(nextRTC, signal) {
	    var pc = nextRTC.preparePeerConnection(nextRTC, signal.from);
		pc['pc'].setRemoteDescription(new RTCSessionDescription({
			type : 'answer',
			sdp : signal.content
		})).then(function(){
			pc['rem'] = true;
		});
    };

    this.close = function(nextRTC, event) {
        nextRTC.signaling.close();
        if(nextRTC.localStream != null){
            nextRTC.localStream.stop();
        }
        if(nextRTC.remoteStream != null){
            nextRTC.remoteStream.stop();
        }
    };

    this.leave = function(){
        var nextRTC = NextRTC.instance;
        nextRTC.request('left');
        nextRTC.signaling.close();
        if(nextRTC.localStream != null){
            nextRTC.localStream.stop();
        }
        if(nextRTC.remoteStream != null){
            nextRTC.remoteStream.stop();
        }
    };

	this.candidate = function(nextRTC, signal) {
	    var pc = nextRTC.preparePeerConnection(nextRTC, signal.from);
   	    pc['pc'].addIceCandidate(new RTCIceCandidate(JSON.parse(signal.content.replace(new RegExp('\'', 'g'), '"'))), success, error);
    }

	this.init = function() {
		this.on('offerRequest', this.offerRequest);
		this.on('answerRequest', this.answerRequest);
		this.on('finalize', this.finalize);
		this.on('candidate', this.candidate);
		this.on('close', this.close);
		this.on('ping', function(){});
	};

	this.onIceCandidate = function(nextRTC, member, event) {
          if (event.candidate) {
            nextRTC.request('candidate', member, JSON.stringify(event.candidate));
          }
        }

	this.init();
};

NextRTC.instance = null;

NextRTC.onReady = function() {
	console.log('It is highly recommended to override method NextRTC.onReady');
};

// it works for new Chrome, Opera and FF
if (document.addEventListener) {
	document.addEventListener('DOMContentLoaded', function() {
		NextRTC.onReady();
	});
}

var error = function(error) {
	console.log('error ' + JSON.stringify(error));
};

var success = function(success) {
	console.log('success ' + JSON.stringify(success));
};
