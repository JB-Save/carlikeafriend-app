import { WelcomeSection } from "../components/WelcomeSection"
import { SearchSection } from "../components/SearchSection"
import { CategoriesSection } from "../components/CategoriesSection"
import { RecommendationSection } from "../components/RecommendationSection"
import "../styles/MainStyle.css"
import "../styles/HomeStyle.css"


export const HomeComponent = () => {
    return (
        <main className="min-vh-100 container-fluid py-4">
            <WelcomeSection/>
            <SearchSection productsPerPage={10} type="search"/>
            <CategoriesSection />
            <RecommendationSection productsPerPage={4} type="recommendation"/>
        </main>

    )
}
