import React, { useState, useEffect } from 'react'
import Cookie from 'js-cookie'
import SmallScreenNavbar from './SmallScreenNavbar'
import FullScreenNavbar from './FullScreenNavbar'

export default function Navbar() {
    const [ jwt, setJwt ] = useState('')
    const [ windowWidth, setWindowWidth ] = useState(1920)

    //used because this needs to be rendered on client
    useEffect(() => {
        setJwt(Cookie.get('jwt'))

        const handleResize = () => setWindowWidth(window.innerWidth)
        window.addEventListener('resize', handleResize)
        handleResize()

        return () => window.removeEventListener('resize', handleResize)
    }, [])

    return (
        windowWidth >= 800
            ? <FullScreenNavbar jwt={jwt} />
            : <SmallScreenNavbar jwt={jwt} />
    )
}
