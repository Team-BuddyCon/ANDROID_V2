package com.yapp.buddycon.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yapp.buddycon.designsystem.R
import com.yapp.buddycon.designsystem.component.tag.DDayTag
import com.yapp.buddycon.designsystem.theme.BuddyConTheme
import com.yapp.buddycon.designsystem.theme.Grey40
import com.yapp.buddycon.designsystem.theme.Grey60
import com.yapp.buddycon.designsystem.theme.Paddings
import com.yapp.buddycon.designsystem.theme.Pink100
import com.yapp.buddycon.domain.model.gifticon.GifticonModel
import com.yapp.buddycon.domain.model.type.GifticonStore
import java.util.Calendar

private val GifticonInfoListModalSheetHeight = 573.dp
private val GifticonInfoListModalSheetRadius = 24.dp
private val GifticonInfoListModalSheetItemElevation = 10.dp
private val GifticonInfoListModalSheetItemRadius = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GifticonInfoListModalSheet(
    modifier: Modifier = Modifier,
    countOfUsableGifticon: Int,
    countOfImminetGifticon: Int,
    gifticonInfos: List<GifticonModel> = listOf()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Paddings.xlarge)
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = Paddings.xlarge)
                .size(32.dp, 4.dp)
                .background(Grey40, RoundedCornerShape(100.dp))
        )
        Text(
            text = stringResource(R.string.modal_sheet_gifticon),
            style = BuddyConTheme.typography.subTitle
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${countOfUsableGifticon}개",
                style = BuddyConTheme.typography.title01
            )
            Text(
                text = String.format(
                    stringResource(R.string.modal_sheet_imminet_gifticon),
                    countOfImminetGifticon
                ),
                modifier = Modifier.padding(start = Paddings.medium, bottom = Paddings.small),
                style = BuddyConTheme.typography.body04.copy(color = Pink100)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(top = Paddings.xlarge)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Paddings.xlarge),
            horizontalArrangement = Arrangement.spacedBy(Paddings.medium)
        ) {
            itemsIndexed(gifticonInfos) { index, item ->
                Column(
                    modifier = Modifier.padding(bottom = if (index == gifticonInfos.lastIndex) Paddings.xlarge else 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = GifticonInfoListModalSheetItemElevation),
                            shape = RoundedCornerShape(GifticonInfoListModalSheetItemRadius)
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        DDayTag(
                            modifier = Modifier
                                .padding(
                                    top = Paddings.medium,
                                    start = Paddings.medium
                                ),
                            dateMillis = item.expirationTime
                        )
                    }
                    Text(
                        text = item.category.value,
                        modifier = Modifier.padding(top = Paddings.large),
                        style = BuddyConTheme.typography.body03.copy(color = Pink100),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = item.name,
                        style = BuddyConTheme.typography.body04,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = "~${item.toExpirationDate()}",
                        modifier = Modifier.padding(top = Paddings.medium),
                        style = BuddyConTheme.typography.body03.copy(color = Grey60)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 255)
@Composable
private fun GifticonInfoListModalSheetPreview() {
    var isShowModal by remember { mutableStateOf(true) }
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.time

    BuddyConTheme {
        GifticonInfoListModalSheet(
            countOfUsableGifticon = 4,
            countOfImminetGifticon = 1,
            gifticonInfos = List(10) {
                GifticonModel(
                    imageUrl = "https://github.com/Team-BuddyCon/ANDROID_V2/assets/34837583/5ab80674-4ffb-4c91-ab10-3743d8c87e58",
                    category = GifticonStore.STARBUCKS,
                    name = "빙그레)바나나맛우유240",
                    expirationTime = (today + 1000 * 60 * 60 * 24L * (-1..366).random())
                )
            }
        )
    }
}
