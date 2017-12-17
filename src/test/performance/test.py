#!/usr/bin/env python3

import asyncio
import websockets
import json

async def hello(i,uri,isCreate):
    async with websockets.connect(uri) as websocket:
        if isCreate:
            x = {
                'from':'',
                'to':'',
                'signal':'create',
                'content':str(i)
            }
        else:
            x = {
                'from':'',
                'to':'',
                'signal':'join',
                'content':str(i)
            }

        s = json.dumps(x);
        await websocket.send(s)
loop = asyncio.get_event_loop()

tasks = []
for i in range(200):
    t = asyncio.ensure_future(hello(i,'ws://localhost:8080/videochat-wo-spring/signaling',True))
    tasks.append(t)
for i in range(200):
    t = asyncio.ensure_future(hello(i,'ws://localhost:8080/videochat-wo-spring/signaling',False))
    tasks.append(t)

loop.run_until_complete(asyncio.wait(tasks))
loop.close()