import React, { useState, useEffect } from 'react'
import Cookie, { set } from 'js-cookie'

export default function index() {
    
    const [ username, setUsername ] = useState('')
    const [ password, setPassword ] = useState('')

    const handleSubmit = async (e) => {
        e.preventDefault()
        
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: username,
                password: password
            })
        }
        const text = await (await fetch('http://localhost:8080/api/v1/jwt/authenticate', requestOptions)).text()
        console.log('jwt: ' + text)
        Cookie.set('jwt', JSON.parse(text).jwt)
    }

    return (
        <div>
            <form onSubmit={e => handleSubmit(e)}>
                <input 
                    type='text'
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                />
                <input 
                    type='text'
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
                <input 
                    type='submit'
                />

            </form>
        </div>

        
    )

}