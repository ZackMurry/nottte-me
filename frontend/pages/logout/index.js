import React, { useEffect } from 'react'
import Navbar from '../../components/Navbar'
import Cookie from 'js-cookie'
import { useRouter, withRouter } from 'next/router'

export default function Logout() {

    const router = useRouter()

    useEffect(() => {
        Cookie.remove('jwt')
        router.push('/')
    }, [])

    return (
        <div>
            <Navbar />

        </div>
        
    )
}
