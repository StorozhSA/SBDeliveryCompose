package ru.skillbranch.sbdelivery.ui.components.toolbars

import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test
import ru.skillbranch.sbdelivery.AppTheme

@ExperimentalTestApi
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
public class DefaultToolbarInstrumentalTests {
    private val textCanBack = "Can back"

    @get:Rule
    public val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Test
    public fun textDefaultToolbarTest() {
        composeTestRule.setContent {
            AppTheme {
                DefaultToolBar(
                    title = textCanBack,
                    cartCount = 10,
                    canBack = true,
                    canSort = true,
                    canCart = true,
                    onClickSort = {},
                    onClickCart = {},
                    onDrawer = {}
                )
            }
        }
        composeTestRule.onRoot().printToLog("TAG")
        composeTestRule.onNodeWithText(textCanBack).assertExists()
    }

    @Test
    public fun buttonsV1DefaultToolbarTest() {
        composeTestRule.setContent {
            AppTheme {
                DefaultToolBar(
                    title = textCanBack,
                    cartCount = 10,
                    canBack = true,
                    canSort = true,
                    canCart = true,
                    onClickSort = {},
                    onClickCart = {},
                    onDrawer = {}
                )
            }
        }
        //composeTestRule.onRoot().printToLog("TAG")
        composeTestRule.onNodeWithContentDescription("Back").assertExists()
        composeTestRule.onNodeWithContentDescription("Sort down").assertExists()
        composeTestRule.onNodeWithContentDescription("Sort up").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Cart").assertExists()
    }

    @Test
    public fun buttonsV2DefaultToolbarTest() {
        composeTestRule.setContent {
            AppTheme {
                DefaultToolBar(
                    title = textCanBack,
                    cartCount = 10,
                    canBack = false,
                    canSort = false,
                    canCart = false,
                    onClickSort = {},
                    onClickCart = {},
                    onDrawer = {}
                )
            }
        }
        //composeTestRule.onRoot().printToLog("TAG")
        composeTestRule.onNodeWithContentDescription("Home").assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Sort down").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Sort up").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Cart").assertDoesNotExist()
    }

    @Test
    public fun buttonsV3DefaultToolbarTest() {
        composeTestRule.setContent {
            AppTheme {
                DefaultToolBar(
                    title = textCanBack,
                    cartCount = 10,
                    canBack = false,
                    canSort = true,
                    canCart = false,
                    onClickSort = {},
                    onClickCart = {},
                    onDrawer = {}
                )
            }
        }
        //composeTestRule.onRoot().printToLog("TAG")
        composeTestRule.onNodeWithContentDescription("Home").assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
        val button = composeTestRule.onNodeWithContentDescription("Sort down").assertExists()
        composeTestRule.onNodeWithContentDescription("Sort up").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Cart").assertDoesNotExist()

        button.assertIsDisplayed()
        button.performClick()
        Thread.sleep(10000)
    }
}
