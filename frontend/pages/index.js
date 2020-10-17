import '../styles/Home.module.css'
import { useEffect, useState } from 'react'
import DefaultLandingPage from '../components/homepage/DefaultLandingPage'
import MobileLandingPage from '../components/homepage/MobileLandingPage'

export default function Home() {
    const [ windowWidth, setWindowWidth ] = useState(1920)

    useEffect(() => {
        const handleResize = () => setWindowWidth(window.innerWidth)
        window.addEventListener('resize', handleResize)
        handleResize()

        return () => window.removeEventListener('resize', handleResize)
    }, [])

    return (
        windowWidth >= 800
            ? <DefaultLandingPage />
            : <MobileLandingPage />
    )
}
