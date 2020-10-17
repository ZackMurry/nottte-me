import Head from "next/head";
import Navbar from "../navbar/Navbar";

export default function MobileLandingPage() {
    return (
        <div
            style={{
                margin: 0, padding: 0, overflowX: 'hidden', overflowY: 'hidden', width: '100%'
            }}
        >
            <Head>
                <title>nottte.me</title>
            </Head>

            <Navbar />
        </div>
    )
}
