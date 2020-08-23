import React, { useState, useEffect } from 'react'
import Cookie from 'js-cookie'

export default function index() {

    const initialJwt = Cookie.get('jwt')

    const [ jwt, setJwt ] = useState(initialJwt ? initialJwt : '')
    const [ response, setResponse ] = useState('')

    useEffect(() => {
        async function getData() {
            if(jwt.length < 10) {
                console.log('unauthenticated')
                return;
            }
            const requestOptions = {
                method: 'GET',
                headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            }
            const text = await (await fetch('http://localhost:8080/user', requestOptions)).text()
            setResponse(text)
        }
        getData()
    }, [])

    return (
        <div>
            {response}
        </div>
        
    )

}