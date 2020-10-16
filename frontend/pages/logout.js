import React, { useEffect } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import Navbar from '../components/navbar/Navbar'

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
