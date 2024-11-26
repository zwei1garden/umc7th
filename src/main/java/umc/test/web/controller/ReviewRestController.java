package umc.test.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import umc.test.apiPayload.ApiResponse;
import umc.test.converter.ReviewConverter;
import umc.test.domain.Review;
import umc.test.service.ReviewService.ReviewCommandService;
import umc.test.service.StoreService.StoreQueryServiceImpl;
import umc.test.validation.annotation.ExistStore;
import umc.test.web.dto.review.ReviewRequestDTO;
import umc.test.web.dto.review.ReviewResponseDTO;
import umc.test.web.dto.store.StoreResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reviews")
@Tag(name = "ReviewRestController", description = "리뷰 관련 API")
public class ReviewRestController {
    private final ReviewCommandService reviewCommandService;
    private final StoreQueryServiceImpl storeQueryService;

    @Operation(summary = "리뷰 추가 API", description = "작성한 리뷰를 추가합니다.")
    @PostMapping("add-review")
    public ApiResponse<ReviewResponseDTO.AddReviewResDTO> createReview(@Valid @RequestBody ReviewRequestDTO.AddReviewReqtDTO request) {
        Review review = reviewCommandService.createReview(request);
        return ApiResponse.onSuccess(ReviewConverter.toReviewResponseDTO(review));
    }

    @Operation(summary = "리뷰 목록 조회 API", description = "사용자가 작성한 리뷰 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "storeId", description = "가게의 아이디, path variable 입니다!")
    })
    @GetMapping("/{storeId}/reviews")
    public ApiResponse<ReviewResponseDTO.ReviewPreViewListDTO> getReviewList(@ExistStore @PathVariable(name = "storeId") Long storeId, @RequestParam(name = "page") Integer page) {
        Page<Review> reviewList = storeQueryService.getReviewList(storeId,page);
        return ApiResponse.onSuccess(ReviewConverter.toReviewPreViewListDTO(reviewList));
    }
}